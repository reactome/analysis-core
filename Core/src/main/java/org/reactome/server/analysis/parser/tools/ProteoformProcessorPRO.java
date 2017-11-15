package org.reactome.server.analysis.parser.tools;

import org.reactome.server.analysis.core.model.Proteoform;
import org.reactome.server.analysis.core.util.MapList;

import java.util.ArrayList;
import java.util.List;

public class ProteoformProcessorPRO {

    /**
     * Format rules:
     * - One proteoform per line
     * - Consists of a sequence block and optional modification blocks
     * - The only mandatory part is the accession number.
     * - There are one or more optional modification blocks
     * - Sequence blocks consist of a UniProtKB accession with an optional isoform indicated by a dash, followed
     * by a comma. And an optional subsequence range separated with a comma.
     * - Each modification block is presented in order from the N-terminal-most amino acid specified.
     * - Within a modification block there are one or more amino acids listed by type and position.
     * - Multiple amino-acids within a block are separated by forward slashes.
     * - Positions of modification are relative to the full length of the isoform.
     * - Missing a subsequence section indicates that the class encompasses either multiple species or isoforms.
     * - Missing modification blocks with a subsequence indicates that the class is defined by subsequence only.
     * - NOTE: In our casse we will only use the accession numbers and set of post translational modifications
     * to identify a particular proteoform, to make our analysis consistent with the rest of the formats.
     * - We allow the position to be null, so that it is also consistent with the rest.
     * <p>
     * The draft of the format is at: doi: 10.1093/nar/gkw1075
     */

    public static void processFile(String input) {
        //TODO
    }

    /**
     * Receives a trimmed line that has already been proved to follow the regex for PRO Proteoform.
     *
     * @param line
     * @param i
     * @return
     */
    public static Proteoform getProteoform(String line, int i) {
        StringBuilder protein = new StringBuilder();
        StringBuilder coordinate = null;
        List<Long> coordinateList;
        StringBuilder mod = null;
        MapList<String, Long> ptms = new MapList<>();

        int pos = 0;
        char c = line.charAt(pos);
        while (c != ':') {        // Read the database name section "UniProtKB:"
            c = line.charAt(++pos);
        }
        c = line.charAt(++pos);
        while (true) {        // Read the accession section
            protein.append(c);
            pos++;
            if (pos >= line.length()) {
                break;
            }
            c = line.charAt(pos);
            if (c != ',') {
                break;
            }
        }           // The proteoform should come at least until here
        pos++;      // Advance after the comma of te accession or out of the string
        if(pos < line.length()){        // If there are still characters
            c = line.charAt(pos);
            if (Character.isDigit(c)) {   // If there is a subsequence, skip it
                while (c != ',') {
                    c = line.charAt(++pos);
                }
            }
        }

        coordinateList = new ArrayList<>();
        while (pos < line.length()) {       //Read the post-translational modifications section
            c = line.charAt(pos);           //While there are characters to read expect: \w{3}-\d+/PTM/PTM,MOD:#####

            if (c == '|') {
                c = line.charAt(++pos);
            }
            while (c != ',') {
                while (c != '-') {
                    c = line.charAt(++pos);
                }
                coordinate = new StringBuilder();
                while (c != ',' && c != '/') {
                    coordinate.append(c);
                }
                coordinateList.add(Long.valueOf(coordinate.toString()));
                c = line.charAt(++pos);
            }
            while (c != ':') {    // Skip the "MOD:"
                c = line.charAt(++pos);
            }
            mod = new StringBuilder();
            for (int I = 0; I < 5; I++) {
                mod.append(line.charAt(++pos));
            }
            pos++;
        }

        return new Proteoform(protein.toString(), ptms);
    }

    public static Proteoform getProteoform(String line) {
        return getProteoform(line, 0);
    }

    public static String getString(Proteoform proteoform) {
        StringBuilder str = new StringBuilder();
        str.append("UniProtKB:" + proteoform.getUniProtAcc());
        if (proteoform.getPTMs().values().size() > 0) {
            str.append(",");
            String[] mods = proteoform.getPTMs().keySet().stream().toArray(String[]::new);
            for (int M = 0; M < mods.length; M++) {
                if (M != 0) {
                    str.append("|");
                }
                Long[] sites = new Long[proteoform.getPTMs().getElements(mods[M]).size()];
                sites = proteoform.getPTMs().getElements(mods[M]).toArray(sites);
                for (int S = 0; S < sites.length; S++) {
                    if (S != 0) {
                        str.append("/");
                    }
                    str.append(getResidue(mods[M]) + "-" + sites[S]);
                }
                str.append(",MOD:" + mods[M]);
            }
        }
        return str.toString();
    }

    private static String getResidue(String mod) {
        switch (mod) {
            case "00010":
            case "01631":
                return "Ala";
            case "00092":
            case "00012":
                return "Asn";
            case "00011":
            case "01632":
                return "Arg";
            case "00113":
            case "00014":
            case "01635":
            case "00094":
                return "Cys";
            case "01637":
                return "Gln";
            case "00015":
            case "00041":
                return "Glu";
            case "01638":
                return "Gly";
            case "00018":
                return "His";
            case "00019":
                return "Ile";
            case "01641":
                return "Leu";
            case "00037":
            case "00130":
            case "00162":
            case "01148":
            case "01914":
            case "00083":
            case "01149":
            case "00064":
                return "Lys";
            case "00023":
                return "Phe";
            case "00038":
            case "00039":
            case "01645":
            case "00024":
                return "Pro";
            case "00046":
            case "01646":
            case "00025":
                return "Ser";
            case "00047":
            case "00813":
            case "00026":
                return "Thr";
            case "00027":
            case "01648":
                return "Trp";
            case "00048":
                return "Tyr";
            case "01650":
                return "Val";
            default:
                return "XXX";
        }
    }
}
