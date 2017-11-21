package org.reactome.server.analysis.parser;

import org.reactome.server.analysis.parser.exception.ParserException;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.reactome.server.analysis.parser.tools.InputPatterns.EXPRESSION_VALUES;

/**
 * Utility class to validate and extract information from PEFF files, specification 1.0.draft25
 * The format is currently in development at: https://github.com/HUPO-PSI/PEFF
 * The specification document: https://github.com/HUPO-PSI/PEFF/tree/master/Specification
 *
 * @author Luis Francisco Hernández Sánchez <luis.sanchez@uib.no>
 */

public class ParserProteoformPEFF extends Parser {

    private String version;
    private boolean isValid;
    private int nDatabases;
    private int nEntries;

    /**
     * Format rules:
     * - Always the first line contains "# PEFF N.N"
     * <p>
     * - Contains two sections:
     * File header section
     * - File description block
     * - Sequence database description block 1
     * ...
     * - Sequence database description block m
     * Individual sequence entries section
     * - Sequence Entry 1 from sequence database 1
     * ...
     * - Sequence Entry n from sequence database 1
     * ...
     * - Sequence Entry 1 from sequence database n
     * ...
     * - Sequence Entry o from sequence database n
     */

    private static final String LINE_JUMP = "\\s*\\r?\\n\\s*";
    private static final String FIRST_LINE = "# PEFF 1.0";
    private static final String FIRST_LINE_FLEXIBLE = "\\s+#\\s*PEFF\\s*1.\\d+";
    private static final String GENERAL_COMMENT = "\\s*#\\s*GeneralComment.+\\s*";
    private static final String GENERAL_COMMENT_EMPTY = "\\s*#\\s*GeneralComment\\s*";
    private static final String UNKNOWN_COMMENT = "\\s*#\\s*GeneralComment.+\\s*";
    private static final String FILE_DESCRIPTION_BLOCK = "";

    private static final String DB_NAME = "# DbName";
    private static final String DB_SOURCE = "# DbSource";
    private static final String NUMBER_OF_ENTRIES = "# NumberOfEntries";
    private static final String SEQUENCE_TYPE = "# SequenceType";
    private static final String DB_VERSION = "# DbVersion";
    private static final String DB_HEADER_BLOCK = "\\s*" + DB_NAME + LINE_JUMP + "(" + +"|" + ")";


    private static final String SEQUENCE_ENTRY = "";

    private static final Pattern PATTERN_PROTEOFORM_PEFF = Pattern.compile(PROTEOFORM_PEFF);
    private static final Pattern PATTERN_PROTEOFORM_PEFF_WITH_EXPRESSION_VALUES = Pattern.compile(PROTEOFORM_PEFF + EXPRESSION_VALUES);

    public static boolean check(String str) {
        Matcher m = PATTERN_PROTEOFORM_PEFF.matcher(str);
        return m.find();
    }

    public static boolean matches_Proteoform_Peff(String str) {
        Matcher m = PATTERN_PROTEOFORM_PEFF.matcher(str);
        return m.matches();
    }

    @Override
    public boolean flexibleCheck() {
        return false;
    }

    @Override
    /**
     * Expects the file to be well formed following the strict rules of PEFF.
     */
    public void parseData(String input) throws ParserException {
        parseDataByLines(input);
    }

    /**
     * Expects the file to be well formed following the strict rules of PEFF.
     * It parses line by line.
     */
    // TODO
    public void parseDataByLines(String input) throws ParserException {
        headerColumnNames.add(DEFAULT_IDENTIFIER_HEADER);

        // Skip the "File header section"
        // For each sequence database information block
        // Read the mandatory header fields: DbName (first), DbVersion, DbSource, NumberOfEntries, SequenceType
        // Read until the end of the database block: "# //"
        // When the

        Scanner in = new Scanner(input);
        String line;

        while(in.hasNextLine()){
            String
            if(line.equals("# //")){
                break;
            }
        }

    }

    /**
     * Expects the file to be well formed following the strict rules of PEFF.
     * It parses line by line.
     */
    // TODO
    public void parseDataByTokens(String input) throws ParserException {
        headerColumnNames.add(DEFAULT_IDENTIFIER_HEADER);

        // Skip the "File header section"
        // For each sequence database information block
        // Read the mandatory header fields: DbName (first), DbVersion, DbSource, NumberOfEntries, SequenceType
        // Read until the end of the database block: "# //"
        // When the

        input.toCharArray();
        Scanner in = new Scanner(input);
        while (in.hasNext()) {
            System.out.println(in.nextLine());
        }
    }

    /**
     * Expects the file to be partially following the rules of PEFF. Enough to extract the sequences, subsequences and PTMs.
     */
    public void parseDataFlexible(String input) throws ParserException {
        headerColumnNames.add(DEFAULT_IDENTIFIER_HEADER);

        // Skip the "File header section"
        // For each sequence database information block
        // Read the mandatory header fields: DbName (first), DbVersion, DbSource, NumberOfEntries, SequenceType
        // Read until the end of the database block: "# //"
        // When the

        Scanner in = new Scanner(input);
        while (in.hasNext()) {
            System.out.println(in.next());
        }
    }
}
