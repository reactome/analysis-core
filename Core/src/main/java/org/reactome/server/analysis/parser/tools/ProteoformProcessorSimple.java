package org.reactome.server.analysis.parser.tools;

import com.google.common.base.CharMatcher;
import org.reactome.server.analysis.core.model.Proteoform;
import org.reactome.server.analysis.core.util.MapList;
import org.reactome.server.analysis.parser.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.reactome.server.analysis.parser.tools.InputPatterns.EXPRESSION_VALUES;

public class ProteoformProcessorSimple {

    public static final String PROTEOFORM_SIMPLE = "([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2})([-]\\d{1,2})?;(\\d{5}:(\\d{1,11}|[Nn][Uu][Ll][Ll]))?(,\\d{5}:(\\d{1,11}|[Nn][Uu][Ll][Ll]))*";
    private static final String ONELINE_MULTIPLE_PROTEOFORM_SIMPLE = "^\\s*" + PROTEOFORM_SIMPLE + "(\\s+" + PROTEOFORM_SIMPLE + ")*\\s*$";
    private static final Pattern PATTERN_PROTEOFORM_SIMPLE = Pattern.compile(PROTEOFORM_SIMPLE);
    private static final Pattern PATTERN_PROTEOFORM_SIMPLE_WITH_EXPRESSION_VALUES = Pattern.compile(PROTEOFORM_SIMPLE + EXPRESSION_VALUES);
    private static final Pattern PATTERN_ONELINE_PROTEOFORM_SIMPLE = Pattern.compile(ONELINE_MULTIPLE_PROTEOFORM_SIMPLE);

    public static boolean matches_Proteoform_Simple(String str) {
        Matcher m = PATTERN_PROTEOFORM_SIMPLE.matcher(str);
        return m.matches();
    }

    public static boolean matches_Proteoform_Simple_With_Expression_Values(String str) {
        Matcher m = PATTERN_PROTEOFORM_SIMPLE_WITH_EXPRESSION_VALUES.matcher(str);
        return m.matches();
    }

    public static boolean matches_oneLine_Simple_Proteoforms(String str){
        Matcher m = PATTERN_ONELINE_PROTEOFORM_SIMPLE.matcher(str);
        return m.matches();
    }

    public static boolean contains_Proteoform_Simple(String str) {
        Matcher m = PATTERN_PROTEOFORM_SIMPLE.matcher(str);
        return m.find();
    }

    public static Proteoform getProteoform(String line) {
        return getProteoform(line, 0, new ArrayList<>());
    }

    public static Proteoform getProteoform(String line, int i, List<String> warningResponses) {

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
            c = line.charAt(pos);
            if (!Character.isDigit(c)) {
                break;
            }
            coordinate = new StringBuilder();
            mod = new StringBuilder();
            //Read a ptm
            while (c != ':') {
                mod.append(c);
                pos++;
                c = line.charAt(pos);
            }
            pos++;
            c = line.charAt(pos);
            while (Character.isDigit(c) || CharMatcher.anyOf("nulNUL").matches(c)) {
                coordinate.append(c);
                pos++;
                if (pos == line.length())
                    break;
                c = line.charAt(pos);
            }
            ptms.add(mod.toString(), (coordinate.toString().toLowerCase().equals("null") ? null : Long.valueOf(coordinate.toString())));
            if (c != ',') {
                break;
            }
            pos++;
        }

        return new Proteoform(protein.toString(), ptms);
    }

    public static String getString(Proteoform proteoform) {
        StringBuilder str = new StringBuilder();
        str.append(proteoform.getUniProtAcc() + ";");
        String[] mods = proteoform.getPTMs().keySet().stream().toArray(String[]::new);
        for (int M = 0; M < mods.length; M++) {
            Long[] sites = new Long[proteoform.getPTMs().getElements(mods[M]).size()];
            sites = proteoform.getPTMs().getElements(mods[M]).toArray(sites);
            for (int S = 0; S < sites.length; S++) {
                if (M != 0 || S != 0) {
                    str.append(",");
                }
                str.append(mods[M] + ":" + sites[S]);
            }
        }
        return str.toString();
    }
}
