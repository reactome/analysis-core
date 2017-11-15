package org.reactome.server.analysis.parser;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.reactome.server.analysis.core.model.AnalysisIdentifier;
import org.reactome.server.analysis.core.model.Proteoform;
import org.reactome.server.analysis.core.util.MapList;
import org.reactome.server.analysis.core.util.Pair;
import org.reactome.server.analysis.parser.exception.ParserException;
import org.reactome.server.analysis.parser.response.Response;
import org.reactome.server.analysis.parser.tools.*;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static org.reactome.server.analysis.parser.tools.InputPatterns.*;
import static org.reactome.server.analysis.parser.tools.ProteoformsProcessor.checkForProteoforms;
import static org.reactome.server.analysis.parser.tools.ProteoformsProcessor.checkForProteoformsWithExpressionValues;

/**
 * Parser for AnalysisData tool v2
 * <p>
 * Supported input formats: Accession lists, Expression matrices, simple proteoform lists, Protein Ontology (PRO) proteoform lists
 */
public class InputFormat_v3 extends InputProcessor {

    private static Logger logger = Logger.getLogger(InputFormat_v3.class.getName());

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        InputFormat_v3.logger = logger;
    }

    /**
     * This is the default header for oneline file and multiple line file
     * Changing here will propagate in both.
     */
    private static final String DEFAULT_IDENTIFIER_HEADER = "";
    private static final String DEFAULT_EXPRESSION_HEADER = "col";

    /**
     * Pride is using colon, we decided to remove it from the parser
     **/
    private static final String HEADER_SPLIT_REGEX = "[\\t,;]+";

    /**
     * Regex for parsing the content when we do not have the header, trying to build a default one
     **/
    private static final String NO_HEADER_DEFAULT_REGEX = "[\\s,;]+";

    /**
     * Regex used to split the content of a single line file
     **/
    private static final String ONE_LINE_CONTENT_SPLIT_REGEX = "[\\s,;]+";

    /**
     * Regex used to split the content of a multiple line file
     * Note also that using + instead of * avoids replacing empty strings and therefore might also speed up the process.
     **/
    private static final String MULTI_LINE_CONTENT_SPLIT_REGEX = "[\\s,;]+";

    /**
     * Regex used to split the columns of a line file, which may be a proteoform
     **/
    private static final String ONE_LINE_CONTENT_SPLIT_REGEX_ONLY_SPACES = "[\\s]+";

    private List<String> headerColumnNames = new LinkedList<>();
    private Set<AnalysisIdentifier> analysisIdentifierSet = new LinkedHashSet<>();
    private boolean hasHeader = false;
    private ProteoformFormat proteoformFormat = ProteoformFormat.NONE;

    public static enum ProteoformFormat {
        NONE,
        UNKNOWN,
        CUSTOM,
        PRO,
        PIR_ID,
        GPMDB
    }


    /**
     * Threshold number for columns, based on the first line we count columns.
     * All the following lines must match this threshold.
     */
    private int thresholdColumn = 0;

    private List<String> errorResponses = new LinkedList<>();
    private List<String> warningResponses = new LinkedList<>();

    /**
     * Ignoring the initial blank lines and start parsing from the
     * first valid line.
     */
    private int startOnLine = 0;

    /**
     * This is the core method. Start point for calling other features.
     * It is split in header and data.
     * <p>
     * ParserException is thrown only when there are errors.
     *
     * @param input file already converted into a String.
     * @throws IOException, ParserException
     */
    public void parseData(String input) throws ParserException {

        String cleanInput = input.trim();
        if (cleanInput.equalsIgnoreCase("")) {        // no data to be analysed
            errorResponses.add(Response.getMessage(Response.EMPTY_FILE));
        } else {        // If there is content in the input
            String[] lines = cleanInput.split("[\r\n]");   // Split lines. Do not add + here. It will remove empty lines

            int firstLineNumber = isOneLineFile(lines);
            if (firstLineNumber >= 0) {
                hasHeader = false;
                analyseOneLineFile(lines[firstLineNumber]);
            } else {
                analyseHeaderColumns(lines);                    // Prepare header
                analyseContent(lines);                          // Prepare content
            }
        }

        if (hasError()) {
            logger.error("Error analysing your data");
            throw new ParserException("Error analysing your data", errorResponses);
        }
    }

    /**
     * ---- FOR VERY SPECIFIC CASES, BUT VERY USEFUL FOR REACTOME ----
     * There're cases where the user inputs a file with one single line to be analysed
     * This method performs a quick view into the file and count the lines. It stops if file has more than one line.
     * p.s empty lines are always ignored
     * To avoid many iteration to the same file, during counting lines the main attributes are being set and used in the
     * analyse content method.
     * This method ignores blank lines,spaces, tabs and so on.
     *
     * @param input the file already trimmed.
     * @return If the file has one valid line returns the number of line. Otherwise returns a negative number.
     */
    private int isOneLineFile(String[] input) {

        int lineNumber = -1;
        int countNonEmptyLines = 0;
        for (int N = 0; N < input.length; N++) {

            // Cleaning the line in other to eliminate blank or spaces spread in the file
            String cleanLine = input[N].trim();

            if (StringUtils.isNotEmpty(cleanLine) || StringUtils.isNotBlank(cleanLine)) {
                countNonEmptyLines++;
                lineNumber = N;

                // We don't need to keep counting...
                if (countNonEmptyLines > 1) {
                    lineNumber = -1;
                    break;
                }
            }
        }
        return lineNumber;
    }

    /**
     * For single line files the rules are:
     * 1- No Expressions Values
     * 2- Cannot start with #, comments
     * 3- Must match this "regular expression" (\delim)?ID((\delimID)* | (\delimNUMBER)* )
     * 4- where \delim can be space, comma, colon, semi-colon or tab.
     * <p>
     * In case it contains proteoforms:
     * 1, 2 still apply
     * Must match the format (\delim)?Proteoform((\delimProteoform)*)
     * Delimiters can be space or tab.
     */
    private void analyseOneLineFile(String line) {
        //Check if the line belongs to a Proteoform
        String regexp = ONE_LINE_CONTENT_SPLIT_REGEX;
        boolean containsProteoform = matches_oneLine_Custom_Proteoforms(line);
        if (containsProteoform) {
            regexp = ONE_LINE_CONTENT_SPLIT_REGEX_ONLY_SPACES;
        }

        /** Note also that using + instead of * avoids replacing empty strings and therefore might also speed up the process. **/

        Pattern p = Pattern.compile(regexp);

        // Line cannot start with # or //
        if (startsWithHeaderSign(line)) {
            errorResponses.add(Response.getMessage(Response.START_WITH_HASH));
            return;
        }

        /** Note that using String.replaceAll() will compile the regular expression each time you call it. **/
        line = p.matcher(line).replaceAll(" ");

        /** StringTokenizer has more performance to offer than String.slit. **/
        StringTokenizer st = new StringTokenizer(line); //space is default delimiter.

        int tokens = st.countTokens();
        if (tokens > 0) {
            headerColumnNames.add(DEFAULT_IDENTIFIER_HEADER);
            while (st.hasMoreTokens()) {
                AnalysisIdentifier rtn = null;
                if (!containsProteoform) {
                    rtn = new AnalysisIdentifier(st.nextToken().trim());
                } else {
                    String[] content = new String[1];
                    content[0] = st.nextToken().trim();
                    ProteoformFormat proteoformFormat = checkForProteoforms(content, 0);
                    Proteoform proteoform = ProteoformsProcessor.getProteoform(content[0], ProteoformFormat.CUSTOM, 1, warningResponses);
                    rtn = new AnalysisIdentifier(proteoform.getUniProtAcc());
                    rtn.setPtms(proteoform.getPTMs());
                }
                analysisIdentifierSet.add(rtn);
            }
        }
    }

    /**
     * Analyse header based on the first line.
     * Headers must start with # or //
     * Note that the lines already come trimmed. Therefore the method  startsWithHeaderSign(headerLine) works.
     *
     * @param data is the file lines
     */
    private void analyseHeaderColumns(String[] data) {
        /**
         * Verify in which line the file content starts. Some cases, file has a bunch of blank line in the firsts lines.
         * StartOnLine will be important in the content analysis. Having this attribute we don't to iterate and ignore
         * blank lines in the beginning.
         */
        String firstLine = "";
        for (int R = 0; R < data.length; R++) {
            if (StringUtils.isNotEmpty(data[R])) {
                firstLine = data[R];
                startOnLine = R;
                break;
            }
        }

        hasHeader = startsWithHeaderSign(firstLine);
        if (hasHeader) {    //
            headerColumnNames = getHeaderLabels(firstLine);
            thresholdColumn = headerColumnNames.size();
        } else {
            //warningResponses.add(Response.getMessage(Response.MALFORMED_HEADER));
            predictFirstLineAsHeader(firstLine);
        }
    }

    /**
     * There're files which may have a header line but malformed.
     * This method analyse the first line and if the columns are not numeric
     * a potential header is present and the user will be notified.
     * At this point it was already checked if the header contained #|// at the beginning, so we are unsure if it is a header.
     *
     * @param firstLine potential header
     */
    private void predictFirstLineAsHeader(String firstLine) {

        int errorInARow = 0;
        List<String> columnNames = new LinkedList<>();

        firstLine = firstLine.replaceAll("^(#|//)", "");

        // Check if it is a regular line or a proteoform line
        String[] content = {firstLine};
        ProteoformFormat proteoformType = checkForProteoformsWithExpressionValues(content, 0);

        // Split the line in chunks of characters
        String[] chunks;
        // If it is a regular line split with [\s,;]+
        /**
         * We cannot use the HEADER REGEX for parsing the header and prepare the default
         * Why? Tab is a delimiter for header, but space isn't. Colon is a delimiter for the content
         * but not for the header.
         **/
        if (proteoformType.equals(ProteoformFormat.NONE)) {
            chunks = firstLine.split(NO_HEADER_DEFAULT_REGEX);
        }
        // If it contains proteoforms then \s+
        else {
            chunks = firstLine.split(ONE_LINE_CONTENT_SPLIT_REGEX_ONLY_SPACES);
        }

        // Count the number of chunks without only digits
        // Add each chunk to the list of column names.
        if (chunks.length > 0) {
            for (String col : chunks) {
                columnNames.add(col.trim());
                if (!isNumeric(col.trim())) {
                    errorInARow++;
                }
            }
        }

        thresholdColumn = chunks.length;

        // If 2 or more chunks of the row contain at least one letter, send warning that this may be a header
        // and use the first row as header.
        if (errorInARow >= 2) {
            hasHeader = true;
            warningResponses.add(Response.getMessage(Response.POTENTIAL_HEADER));
            headerColumnNames = columnNames;
        } else {
            // just skip the predictable header and use the default one
            hasHeader = false;
            warningResponses.add(Response.getMessage(Response.NO_HEADER));

            buildDefaultHeader(chunks.length);
        }
    }

    /**
     * The default header will be built based on the first line.
     * Example: For colsLength 5 the result would be:
     * ["", "col1", "col2", "col3", "col4"]
     *
     * @param colsLength
     */
    private void buildDefaultHeader(Integer colsLength) {
        thresholdColumn = colsLength;

        headerColumnNames.add(DEFAULT_IDENTIFIER_HEADER);
        for (int i = 1; i < colsLength; i++) {
            headerColumnNames.add(DEFAULT_EXPRESSION_HEADER + i);
        }
    }

    /**
     * Analyse all the data itself.
     * Replace any character like space, comma, semicolon, tab into a space and then replace split by space.
     *
     * @param content line array
     */
    private void analyseContent(String[] content) {
        if (hasHeader) {
            startOnLine += 1;
        }

        // Check for input type Normal/Proteoform using the first lines
        if (headerColumnNames.size() == 1) {
            proteoformFormat = checkForProteoforms(content, startOnLine);
        } else {
            proteoformFormat = checkForProteoformsWithExpressionValues(content, startOnLine);
        }

        // Process all lines
        if (proteoformFormat == ProteoformFormat.NONE) {      // Continue with the normal content processing from Reactome V2

            // Set the regular expression to separate each expression value in a single row
            String regexp = MULTI_LINE_CONTENT_SPLIT_REGEX;
            Pattern p = Pattern.compile(regexp);

            for (int i = startOnLine; i < content.length; ++i) {
                String line = content[i].trim();
                if (line.isEmpty()) {
                    warningResponses.add(Response.getMessage(Response.EMPTY_LINE, i + 1));
                    continue;
                }
//                analyseContentLineNormal(line, i);

                /** Note that using String.replaceAll() will compile the regular expression each time you call it. **/
                line = p.matcher(line).replaceAll(" "); // slow slow slow

                // StringTokenizer has better performance than String.split().
                StringTokenizer st = new StringTokenizer(line); //space is default delimiter.

                int tokens = st.countTokens();
                if (tokens > 0) {
                    /**
                     * analyse if each line has the same amount of columns as the threshold based on first line, otherwise
                     * an error will be reported.
                     */
                    if (thresholdColumn == tokens) {
                        String first = st.nextToken();

                        AnalysisIdentifier rtn = new AnalysisIdentifier(first);

                        int j = 1;
                        while (st.hasMoreTokens()) {
                            String token = st.nextToken().trim();
                            if (isNumeric(token)) {
                                rtn.add(Double.valueOf(token));
                            } else {
                                warningResponses.add(Response.getMessage(Response.INLINE_PROBLEM, i + 1, j + 1));
                            }
                            j++;
                        }
                        analysisIdentifierSet.add(rtn);
                    } else {
                        errorResponses.add(Response.getMessage(Response.COLUMN_MISMATCH, i + 1, thresholdColumn, tokens));
                    }
                }
            }
        } else {
            // Process the content as proteoform lines

            // Set the regular expression to separate each expression value in a single row
            String regexp = ONE_LINE_CONTENT_SPLIT_REGEX_ONLY_SPACES;
            Pattern p = Pattern.compile(regexp);

            for (int i = startOnLine; i < content.length; ++i) {
                String line = content[i].trim();
                if (line.isEmpty()) {
                    warningResponses.add(Response.getMessage(Response.EMPTY_LINE, i + 1));
                    continue;
                }

                line = p.matcher(line).replaceAll(" ");

                analyseContentLineProteoform(line, proteoformFormat, i);
            }
        }
    }

    /**
     * Analyse a content line in the way it was in Reactome V2
     *
     * @param line The content line itself
     * @param i    Line number in the content of the input file
     */
    private void analyseContentLineNormal(String line, int i) {
        long start = System.nanoTime();
        /** Note also that using + instead of * avoids replacing empty strings and therefore might also speed up the process. **/
        String regexp = MULTI_LINE_CONTENT_SPLIT_REGEX;

        Pattern p = Pattern.compile(regexp);

        // Line cannot start with # or //
//        if (startsWithHeaderSign(line)) {
//            errorResponses.add(Response.getMessage(Response.START_WITH_HASH));
//            return;
//        }

        /** Note that using String.replaceAll() will compile the regular expression each time you call it. **/
        line = p.matcher(line).replaceAll(" "); // slow slow slow

        // StringTokenizer has better performance than String.split().
        StringTokenizer st = new StringTokenizer(line); //space is default delimiter.

        int tokens = st.countTokens();
        if (tokens > 0) {
            /**
             * analyse if each line has the same amount of columns as the threshold based on first line, otherwise
             * an error will be reported.
             */
            if (thresholdColumn == tokens) {
                String first = st.nextToken();

                AnalysisIdentifier rtn = new AnalysisIdentifier(first);

                int j = 1;
                while (st.hasMoreTokens()) {
                    String token = st.nextToken().trim();
                    if (isNumeric(token)) {
                        rtn.add(Double.valueOf(token));
                    } else {
                        warningResponses.add(Response.getMessage(Response.INLINE_PROBLEM, i + 1, j + 1));
                    }
                    j++;
                }
                analysisIdentifierSet.add(rtn);
            } else {
                errorResponses.add(Response.getMessage(Response.COLUMN_MISMATCH, i + 1, thresholdColumn, tokens));
            }
        }

        long end = System.nanoTime();
        logger.debug("Elapsed time on Analysing line: " + (end - start) + ".ms");
    }

    /**
     * Analyse a content line with one proteoform and possibly expression values.
     * Assumes that the line comes trimmed and the sets of consecutive spaces have been replaced for a single space.
     *
     * @param line   The content line itself
     * @param format The type of proteoform format that to parse the lines
     * @param i      Line number in the content of the input file
     */
    private void analyseContentLineProteoform(String line, ProteoformFormat format, int i) {

       Proteoform proteoform = ProteoformsProcessor.getProteoform(line, format, i, warningResponses);

        // StringTokenizer has better performance than String.split().
        StringTokenizer st = new StringTokenizer(line); //space is default delimiter.

        int tokens = st.countTokens();
        if (tokens > 0) {
            // analyse if each line has the same amount of columns as the threshold based on first line
            if (thresholdColumn == tokens) {
                String first = st.nextToken();
                AnalysisIdentifier rtn = new AnalysisIdentifier(proteoform.getUniProtAcc());
                rtn.setPtms(proteoform.getPTMs());

                int j = 1;
                while (st.hasMoreTokens()) {
                    String token = st.nextToken().trim();
                    if (isNumeric(token)) {
                        rtn.add(Double.valueOf(token));
                    } else {
                        warningResponses.add(Response.getMessage(Response.INLINE_PROBLEM, i + 1, j + 1));
                    }
                    j++;
                }
                analysisIdentifierSet.add(rtn);
            } else {
                errorResponses.add(Response.getMessage(Response.COLUMN_MISMATCH, i + 1, thresholdColumn, tokens));
            }
        }
    }



    private static Pair<String, MapList<String, Long>> getProteoformGPMDB(String line, int i) {
        throw new NotImplementedException("Missing implementation for getProteoformGPMDB");
    }

    private static boolean startsWithHeaderSign(String line) {
        return line.startsWith("#") || line.startsWith("//");
    }

    /**
     * Get header labels and also define a standard pattern in the column length
     *
     * @param line The line to be analysed as a header
     */
    private List<String> getHeaderLabels(String line) {

        List<String> headerLabelsList = new ArrayList<>();

        // remove chars which categorizes a comment.
        line = line.replaceAll("^(#|//)", "");

        // Split header line by our known delimiters
        String[] cols = line.split(HEADER_SPLIT_REGEX);

        for (String columnName : cols) {
            headerLabelsList.add(StringEscapeUtils.escapeJava(columnName.trim()));
        }
        return headerLabelsList;
    }

    public List<String> getHeaderColumnNames() {
        return headerColumnNames;
    }

    public Set<AnalysisIdentifier> getAnalysisIdentifierSet() {
        return analysisIdentifierSet;
    }

    /**
     * An easy handy method for determining if the parse succeeded
     *
     * @return true if data is wrong, false otherwise
     */
    public boolean hasError() {
        return errorResponses.size() >= 1;
    }

    /**
     * This is of error messages must be associated with a ParserException.
     *
     * @return list of error messages.
     */
    public List<String> getErrorResponses() {
        return errorResponses;
    }

    /**
     * List of warning messages, process will go on, just show what happened in the parser in terms of
     * blank lines or ignored values.
     *
     * @return list of warning messages.
     */
    public List<String> getWarningResponses() {
        return warningResponses;
    }

    /**
     * Instead of calling the Double.valueOf(...) in a try-catch statement and many of the checks to fail due to
     * not being a number then performance of this mechanism will not be great, since you're relying upon
     * exceptions being thrown for each failure, which is a fairly expensive operation.
     * <p>
     * An alternative approach may be to use a regular expression to check for validity of being a number:
     *
     * @param str
     * @return true if is Number
     */
    public boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }

        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
}
