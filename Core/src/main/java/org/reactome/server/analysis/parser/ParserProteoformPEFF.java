package org.reactome.server.analysis.parser;

import org.reactome.server.analysis.parser.exception.ParserException;

import java.io.IOException;
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
     *
     * - Contains two sections:
     *      File header section
     *          - File description block
     *          - Sequence database description block 1
     *          ...
     *          - Sequence database description block m
     *      Individual sequence entries section
     *          - Sequence Entry 1 from sequence database 1
     *          ...
     *          - Sequence Entry n from sequence database 1
     *          ...
     *          - Sequence Entry 1 from sequence database n
     *          ...
     *          - Sequence Entry o from sequence database n
     */

    private static final String PROTEOFORM_PEFF = "# PEFF 1.0";
    private static final String PROTEOFORM_PEFF_FLEXIBLE = "\\s+#\\s*PEFF\\s*1.\\d+";

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
    public void parseData(String input) throws ParserException {

    }
}
