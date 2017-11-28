package org.reactome.server.analysis.parser.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.reactome.server.analysis.parser.ParserProteoformPRO.matches_Proteoform_Pro_With_Expression_Values;
import static org.reactome.server.analysis.parser.ParserProteoformSimple.matches_Proteoform_Simple_With_Expression_Values;

public class InputPatterns {

    private static final String FLOAT_POINT_NUMBER = "[+-]?([0-9]*[.])?[0-9]+";
    private static final String NUMERIC = "-?\\d+(\\.\\d+)?";
    public static final String SPACES = "[\\s]+";  //Regex used to split the content of a single line file. Note also that using + instead of * avoids replacing empty strings and therefore might also speed up the process.
    public static final String EXPRESSION_VALUES = "(\\s+" + FLOAT_POINT_NUMBER + ")*\\s*";
    public static final String PROTEIN_UNIPROT = "([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2})([-]\\d{1,2})?";
    public static final String PROTEIN_ENSEMBL = "ENSP\\d{11}";
    private static final String PROTEOFORM_PIR = "^PR:\\d{9}\\d*\\s*";
    private static final String PROTEOFORM_GPMDB = "(?i)^((([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2})([-]\\d+)?)|(ENSP\\d{11})):pm.([A-Z]\\d+[+=](([A-Z][a-z]+)|(MOD:\\d{5}))(\\/(([A-Z][a-z]+)|(MOD:\\d{5})))*;)+\\s*";    // GPMDB (http://wiki.thegpm.org/wiki/Nomenclature_for_the_description_of_protein_sequence_modifications)

    private static final Pattern PATTERN_NUMERIC = Pattern.compile(NUMERIC);
    public static final Pattern PATTERN_SPACES = Pattern.compile(SPACES);
    private static final Pattern PATTERN_PROTEIN_UNIPROT = Pattern.compile(PROTEIN_UNIPROT);
    private static final Pattern PATTERN_PROTEIN_ENSEMBL = Pattern.compile(PROTEIN_ENSEMBL);
    private static final Pattern PATTERN_PROTEOFORM_PIR = Pattern.compile(PROTEOFORM_PIR);
    private static final Pattern PATTERN_PROTEOFORM_GPMDB = Pattern.compile(PROTEOFORM_GPMDB);
    private static final Pattern PATTERN_PROTEIN_UNIPROT_WITH_EXPRESSION_VALUES = Pattern.compile(PROTEIN_UNIPROT + EXPRESSION_VALUES);
    private static final Pattern PATTERN_PROTEIN_ENSEMBL_WITH_EXPRESSION_VALUES = Pattern.compile(PROTEIN_ENSEMBL + EXPRESSION_VALUES);
    private static final Pattern PATTERN_PROTEOFORM_PIR_WITH_EXPRESSION_VALUES = Pattern.compile(PROTEOFORM_PIR + EXPRESSION_VALUES);
    private static final Pattern PATTERN_PROTEOFORM_GPMDB_WITH_EXPRESSION_VALUES = Pattern.compile(PROTEOFORM_GPMDB + EXPRESSION_VALUES);

    public static boolean matches_Spaces(String str) {
        Matcher m = PATTERN_SPACES.matcher(str);
        return m.matches();
    }

    public static boolean matches_Protein_Uniprot(String str) {
        Matcher m = PATTERN_PROTEIN_UNIPROT.matcher(str);
        return m.matches();
    }

    public static boolean matches_Protein_Ensembl(String str) {
        Matcher m = PATTERN_PROTEIN_ENSEMBL.matcher(str);
        return m.matches();
    }


    public static boolean matches_Proteoform_Pir(String str) {
        Matcher m = PATTERN_PROTEOFORM_PIR.matcher(str);
        return m.matches();
    }

    public static boolean matches_Proteoform_Gpmdb(String str) {
        Matcher m = PATTERN_PROTEOFORM_GPMDB.matcher(str);
        return m.matches();
    }

    public static boolean matches_Protein_Uniprot_With_Expression_Values(String str) {
        Matcher m = PATTERN_PROTEIN_UNIPROT_WITH_EXPRESSION_VALUES.matcher(str);
        return m.matches();
    }

    public static boolean matches_Protein_Ensembl_With_Expression_Values(String str) {
        Matcher m = PATTERN_PROTEIN_ENSEMBL_WITH_EXPRESSION_VALUES.matcher(str);
        return m.matches();
    }

    public static boolean matches_Proteoform_Pir_With_Expression_Values(String str) {
        Matcher m = PATTERN_PROTEOFORM_PIR_WITH_EXPRESSION_VALUES.matcher(str);
        return m.matches();
    }

    public static boolean matches_Proteoform_Gpmdb_With_Expression_Values(String str) {
        Matcher m = PATTERN_PROTEOFORM_GPMDB_WITH_EXPRESSION_VALUES.matcher(str);
        return m.matches();
    }

    public static boolean isProteoformWithExpression(String str) {
        if (matches_Proteoform_Simple_With_Expression_Values(str)
                || matches_Proteoform_Pro_With_Expression_Values(str)
                || matches_Proteoform_Gpmdb_With_Expression_Values(str)
                || matches_Proteoform_Pir_With_Expression_Values(str)) {
            return true;
        }
        return false;
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
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        Matcher m = PATTERN_NUMERIC.matcher(str);
        return m.matches();
    }

    public static Long interpretCoordinateString(String s) {
        if (s == null) {
            return null;
        }
        if (s.length() == 0) {
            return null;
        }
        if (s.equals("?")) {
            return null;
        }
        if(s.toLowerCase().equals("null")){
            return null;
        }
        return Long.valueOf(s);
    }
}
