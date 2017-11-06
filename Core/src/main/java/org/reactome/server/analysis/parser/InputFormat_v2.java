package org.reactome.server.analysis.parser;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.reactome.server.analysis.core.model.AnalysisIdentifier;
import org.reactome.server.analysis.core.util.MapList;
import org.reactome.server.analysis.core.util.Pair;
import org.reactome.server.analysis.parser.exception.ParserException;
import org.reactome.server.analysis.parser.response.Response;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Parser for AnalysisData tool v2
 *
 * Supported input formats: Accession lists, Expression matrices, simple proteoform lists, Protein Ontology (PRO) proteoform lists
 *
 */
public class InputFormat_v2 extends InputProcessor{

    private static Logger logger = Logger.getLogger(InputFormat_v2.class.getName());

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        InputFormat_v2.logger = logger;
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

    /**
     * Regex for Protein identifiers
     */
    public static final String UNIPROT_ID = "[OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2}";
    public static final String ENSEMBL_ID = "ENSP\\d{11}";

    /**
     * Regex for Proteoforms
     */
    public static final String PROTEOFORM_CUSTOM = "^([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2})([-]\\d{1,2})?;(\\d{5}:(\\d{1,11}|[Nn][Uu][Ll][Ll]))?(,\\d{5}:(\\d{1,11}|[Nn][Uu][Ll][Ll]))*";
    public static final String PROTEOFORM_PROTEIN_ONTOLOGY = "^UniProtKB:([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2})[-]?\\d?(,(\\d+-\\d+)?(,([A-Z][a-z]{2}-\\d+(\\/[A-Z][a-z]{2}-\\d+)*,MOD:\\d{5}(\\|[A-Z][a-z]{2}-\\d+(\\/[A-Z][a-z]{2}-\\d+)*,MOD:\\d{5})*)?)?)?\\s*";
    public static final String PROTEOFORM_PIR_ID = "^PR:\\d{9}\\d*\\s*";
    public static final String PROTEOFORM_GPMDB = "(?i)^((([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2})([-]\\d+)?)|(ENSP\\d{11})):pm.([A-Z]\\d+[+=](([A-Z][a-z]+)|(MOD:\\d{5}))(\\/(([A-Z][a-z]+)|(MOD:\\d{5})))*;)+\\s*";    // GPMDB (http://wiki.thegpm.org/wiki/Nomenclature_for_the_description_of_protein_sequence_modifications)

    public static final String FLOAT_POINT_NUMBER = "[+-]?([0-9]*[.])?[0-9]+";
    public static final String EXPRESSION_VALUES = "(\\s+" + FLOAT_POINT_NUMBER + ")*\\s*";

    private enum ProteoformFormat {
        NONE,
        UNKNOWN,
        CUSTOM,
        PROTEIN_ONTOLOGY,
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
    public void parseData(String input) throws IOException, ParserException {

        String clean = input.trim();
        if (clean.equalsIgnoreCase("")) {       // no data to be analysed
            errorResponses.add(Response.getMessage(Response.EMPTY_FILE));
        } else {        // If there is content in the input
            // Split lines
            String[] lines = input.split("[\r\n]"); // Do not add + here. It will remove empty lines

            // check whether one line file is present and parse
            boolean isOneLine = isOneLineFile(lines);
            if (!isOneLine) {
                // Prepare header
                analyseHeaderColumns(lines);

                // Prepare content
                analyseContent(lines);
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
     * @param input the file
     * @return true if file has one line, false otherwise.
     */
    private boolean isOneLineFile(String[] input) {

        int countNonEmptyLines = 0;
        String validLine = "";

        for (int i = 0; i < input.length; i++) {
            // Cleaning the line in other to eliminate blank or spaces spread in the file
            String cleanLine = input[i].trim();
            if (StringUtils.isNotEmpty(cleanLine) || StringUtils.isNotBlank(cleanLine)) {
                countNonEmptyLines++;

                // Line without parsing - otherwise we can't eliminate blank space and tabs spread in the file.
                validLine = input[i];

                // We don't need to keep counting...
                if (countNonEmptyLines > 1) {
                    return false;
                }
            }
        }

        hasHeader = false;

        analyseOneLineFile(validLine);

        return true;
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
        boolean containsProteoform = isProteoformWithExpression(line);
        if (containsProteoform) {
            regexp = ONE_LINE_CONTENT_SPLIT_REGEX_ONLY_SPACES;
        }

        /** Note also that using + instead of * avoids replacing empty strings and therefore might also speed up the process. **/

        Pattern p = Pattern.compile(regexp);

        // Line cannot start with # or //
        if (hasHeaderLine(line)) {
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
                if (containsProteoform) {
                    String[] content = new String[1];
                    content[0] = st.nextToken().trim();
                    ProteoformFormat proteoformFormat = checkForProteoforms(content, 0);
                    analyseContentLineProteoform(content[0], proteoformFormat, 1);
                } else {
                    AnalysisIdentifier rtn = new AnalysisIdentifier(st.nextToken().trim());
                    analysisIdentifierSet.add(rtn);
                }
            }
        }
    }

    /**
     * Analyse header based on the first line.
     * Headers must start with # or //
     *
     * @param data is the file lines
     */
    private void analyseHeaderColumns(String[] data) {
        /**
         * Verify in which line the file content starts. Some cases, file has a bunch of blank line in the firsts lines.
         * StartOnLine will be important in the content analysis. Having this attribute we don't to iterate and ignore
         * blank lines in the beginning.
         */
        String headerLine = "";
        for (int i = 0; i < data.length; i++) {
            if (StringUtils.isNotEmpty(data[i])) {
                headerLine = data[i];
                startOnLine = i;
                break;
            }
        }

        if (hasHeaderLine(headerLine)) {
            // parse header line
            getHeaderLabel(headerLine);
            hasHeader = true;
        } else {
            //warningResponses.add(Response.getMessage(Response.MALFORMED_HEADER));
            predictFirstLineAsHeader(headerLine);
        }
    }

    /**
     * There're files which may have a header line but malformed.
     * This method analyse the first line and if the columns are not number
     * a potential header is present and the user will be notified
     *
     * @param firstLine potential header
     */
    private void predictFirstLineAsHeader(String firstLine) {
        int errorInARow = 0;

        List<String> columnNames = new LinkedList<>();

        firstLine = firstLine.replaceAll("^(#|//)", "");

        String[] content = {firstLine};
        ProteoformFormat proteoformType = checkForProteoforms(content, 0);
        if (proteoformType != ProteoformFormat.NONE && proteoformType != ProteoformFormat.UNKNOWN) {

            if (proteoformType.equals(ProteoformFormat.CUSTOM)) {
                firstLine = firstLine.replaceAll( PROTEOFORM_CUSTOM, "proteoform");
            } else if (proteoformType.equals(ProteoformFormat.PROTEIN_ONTOLOGY)) {
                firstLine = firstLine.replaceAll(PROTEOFORM_PROTEIN_ONTOLOGY, "proteoform");
            } else if (proteoformType.equals(ProteoformFormat.PIR_ID)) {
                firstLine = firstLine.replaceAll(PROTEOFORM_PIR_ID, "proteoform");
            }
        }

        /**
         * We cannot use the HEADER REGEX for parsing the header and prepare the default
         * Why? Tab is a delimiter for header, but space isn't. Colon is a delimiter for the content
         * but not for the header.
         **/
        String[] data = firstLine.split(NO_HEADER_DEFAULT_REGEX);

        if (data.length > 0) {
            for (String col : data) {
                columnNames.add(col.trim());
                if (!isNumeric(col.trim())) {
                    errorInARow++;
                }
            }
        }

        thresholdColumn = data.length;

        if (errorInARow >= 3) {
            hasHeader = true;
            warningResponses.add(Response.getMessage(Response.POTENTIAL_HEADER));

            headerColumnNames = columnNames;
        } else {
            // just skip the predictable header and use the default one
            warningResponses.add(Response.getMessage(Response.NO_HEADER));

            buildDefaultHeader(data.length);
        }
    }

    /**
     * The default header will be built based on the first line.
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
        proteoformFormat = checkForProteoforms(content, startOnLine);

        // Process all lines
        if (proteoformFormat == ProteoformFormat.NONE) {      // Continue with the normal content processing from Reactome V2

            for (int i = startOnLine; i < content.length; ++i) {
                String line = content[i].trim();
                if (line.isEmpty()) {
                    warningResponses.add(Response.getMessage(Response.EMPTY_LINE, i + 1));
                    continue;
                }
//                analyseContentLineNormal(line, i);
                /** Note also that using + instead of * avoids replacing empty strings and therefore might also speed up the process. **/
                String regexp = MULTI_LINE_CONTENT_SPLIT_REGEX;

                Pattern p = Pattern.compile(regexp);

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
        } else {                                                // Process the content as proteoform lines
            for (int i = startOnLine; i < content.length; ++i) {
                String line = content[i].trim();
                if (line.isEmpty()) {
                    warningResponses.add(Response.getMessage(Response.EMPTY_LINE, i + 1));
                    continue;
                }
                analyseContentLineProteoform(line, proteoformFormat, i);
            }
        }
    }

    /**
     * Verifies if a set of content lines follows a proteoform format.
     * Decides using the first 5 lines. If all first lines match the same proteoform format it returns that type.
     * If they all do not match in format, even if they are all proteoforms, it throws a format exception.
     *
     * @param content     Array of content lines to process
     * @param startOnLine Offset to ignore the first 'startOnLine' lines.
     * @return ProteoformFormat instance indicating the format. If ProteoformFormat.NONE is returned,
     * the content lines are not proteoforms and should be processed as usual in Reactome V2. Returns unknown if all lines
     * are empty.
     */
    private ProteoformFormat checkForProteoforms(String[] content, int startOnLine) {
        int nonHeaderContentLines = content.length - startOnLine;
        ProteoformFormat resultFormat = ProteoformFormat.UNKNOWN;
        int linesChecked = 0;
        for (int i = startOnLine; i < content.length && linesChecked < 5; ++i) {
            String line = content[i].trim();
            if (line.isEmpty()) {
                //warningResponses.add(Response.getMessage(Response.EMPTY_LINE, i + 1));
                continue;
            }
            if (line.matches(PROTEOFORM_CUSTOM)) {
                if (resultFormat == ProteoformFormat.UNKNOWN) {
                    resultFormat = ProteoformFormat.CUSTOM;
                } else if (resultFormat != ProteoformFormat.CUSTOM) {
                    //errorResponses.add(Response.getMessage(Response.PROTEOFORM_MISMATCH, i + 1));
                    return ProteoformFormat.NONE;
                }
            } else if (line.matches(PROTEOFORM_PROTEIN_ONTOLOGY)) {
                if (resultFormat == ProteoformFormat.UNKNOWN) {
                    resultFormat = ProteoformFormat.PROTEIN_ONTOLOGY;
                } else if (resultFormat != ProteoformFormat.PROTEIN_ONTOLOGY) {
                    return ProteoformFormat.NONE;
                }
            } else if (line.matches(PROTEOFORM_PIR_ID)) {
                if (resultFormat == ProteoformFormat.UNKNOWN) {
                    resultFormat = ProteoformFormat.PIR_ID;
                } else if (resultFormat != ProteoformFormat.PIR_ID) {
                    return ProteoformFormat.NONE;
                }
            } else if (line.matches(PROTEOFORM_GPMDB)) {
                if (resultFormat == ProteoformFormat.UNKNOWN) {
                    resultFormat = ProteoformFormat.GPMDB;
                } else if (resultFormat != ProteoformFormat.GPMDB) {
                    return ProteoformFormat.NONE;
                }
            } else {
                if (resultFormat == ProteoformFormat.UNKNOWN) {
                    resultFormat = ProteoformFormat.NONE;
                } else if (resultFormat != ProteoformFormat.NONE) {
                    return ProteoformFormat.NONE;
                }
            }
            linesChecked++;
        }

        return resultFormat;
    }

    private static boolean isProteoform(String str) {
        if (str.matches(PROTEOFORM_CUSTOM)
                || str.matches(PROTEOFORM_PROTEIN_ONTOLOGY)
                || str.matches(PROTEOFORM_GPMDB)
                || str.matches(PROTEOFORM_PIR_ID)) {
            return true;
        }
        return false;
    }

    private static boolean isProteoformWithExpression(String str) {
        if (str.matches(PROTEOFORM_CUSTOM + EXPRESSION_VALUES)
                || str.matches(PROTEOFORM_PROTEIN_ONTOLOGY + EXPRESSION_VALUES)
                || str.matches(PROTEOFORM_GPMDB + EXPRESSION_VALUES)
                || str.matches(PROTEOFORM_PIR_ID + EXPRESSION_VALUES)) {
            return true;
        }
        return false;
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
//        if (hasHeaderLine(line)) {
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
     * Analyse a content line as a proteoform
     *
     * @param line   The content line itself
     * @param format The type of proteoform format that to parse the lines
     * @param i      Line number in the content of the input file
     */
    private void analyseContentLineProteoform(String line, ProteoformFormat format, int i) {
        Pair<String, MapList<String, Long>> proteoform = null;
        switch (format) {
            case CUSTOM:
                if (!line.matches(PROTEOFORM_CUSTOM + EXPRESSION_VALUES)) {
                    warningResponses.add(Response.getMessage(Response.INLINE_PROBLEM, i + 1, 1));
                } else {
                    proteoform = getProteoformCustom(line, i + 1);
                }
                break;
            case PROTEIN_ONTOLOGY:
                if (!line.matches(PROTEOFORM_PROTEIN_ONTOLOGY + EXPRESSION_VALUES)) {
                    warningResponses.add(Response.getMessage(Response.INLINE_PROBLEM, i + 1, 1));
                } else {
                    proteoform = getProteoformProteinOntology(line, i + 1);
                }
                break;
            case PIR_ID:
                if (!line.matches(PROTEOFORM_PIR_ID + EXPRESSION_VALUES)) {
                    warningResponses.add(Response.getMessage(Response.INLINE_PROBLEM, i + 1, 1));
                } else {
                    proteoform = getProteoformPIR(line, i + 1);
                }
                break;
            case GPMDB:
                if (!line.matches(PROTEOFORM_GPMDB + EXPRESSION_VALUES)) {
                    warningResponses.add(Response.getMessage(Response.INLINE_PROBLEM, i + 1, 1));
                } else {
                    proteoform = getProteoformGPMDB(line, i + 1);
                }
                break;
            case NONE:
                throw new RuntimeException("The line " + i + " should marked as a proteoform.");
        }

//        System.out.println(proteoform);
        AnalysisIdentifier rtn = new AnalysisIdentifier(proteoform.getFst());
        rtn.setPtms(proteoform.getSnd());
        analysisIdentifierSet.add(rtn);
    }

    private Pair<String, MapList<String, Long>> getProteoformCustom(String line, int i) {

        StringBuilder protein = new StringBuilder();
        StringBuilder coordinate = null;
        StringBuilder mod = null;
        MapList<String, Long> ptms = new MapList<>();

        // Get the identifier
        // Read until end of line or semicolon
        int pos = 0;
        char c = line.charAt(pos);
        while (c != ';') {
            protein.append(c);
            pos++;
            if (pos == line.length())
                break;
            c = line.charAt(pos);
        }
        pos++;
        if (protein.length() == 0) {
            warningResponses.add(Response.getMessage(Response.INLINE_PROBLEM, i + 1, 1));
            throw new RuntimeException("Problem parsing line " + line);
        }

        // Get ptms one by one
        //While there are characters

        while (pos < line.length()) {
            coordinate = new StringBuilder();
            mod = new StringBuilder();
            //Read a ptm
            c = line.charAt(pos);
            while (c != ':') {
                mod.append(c);
                pos++;
                c = line.charAt(pos);
            }
            pos++;
            c = line.charAt(pos);
            while (c != ',') {
                coordinate.append(c);
                pos++;
                if (pos == line.length())
                    break;
                c = line.charAt(pos);
            }
            ptms.add(mod.toString(), (coordinate.toString().toLowerCase().equals("null") ? null : Long.valueOf(coordinate.toString())));
            pos++;
        }

        return new Pair<>(protein.toString(), ptms);
    }

    private static Pair<String, MapList<String, Long>> getProteoformProteinOntology(String line, int i) {
        throw new NotImplementedException("Missing implementation for getProteoformProteinOntology");
    }

    private static Pair<String, MapList<String, Long>> getProteoformPIR(String line, int i) {
        throw new NotImplementedException("Missing implementation for getProteoformPIR");
    }

    private static Pair<String, MapList<String, Long>> getProteoformGPMDB(String line, int i) {
        throw new NotImplementedException("Missing implementation for getProteoformGPMDB");
    }

    private static boolean hasHeaderLine(String line) {
        return line.startsWith("#") || line.startsWith("//");
    }

    /**
     * Get header labels and also define a standard pattern in the column length
     *
     * @param line The line to be analysed as a header
     */
    private void getHeaderLabel(String line) {
        // remove chars which categorizes a comment.
        line = line.replaceAll("^(#|//)", "");

        // Split header line by our known delimiters
        String[] cols = line.split(HEADER_SPLIT_REGEX);

        thresholdColumn = cols.length;

        for (String columnName : cols) {
            headerColumnNames.add(StringEscapeUtils.escapeJava(columnName.trim()));
        }
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
