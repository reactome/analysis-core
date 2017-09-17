package org.reactome.server.analysis.tools;

import org.reactome.server.analysis.core.util.Pair;
import org.reactome.server.graph.service.GeneralService;
import org.reactome.server.graph.service.SchemaService;
import org.reactome.server.graph.utils.ReactomeGraphCore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExampleProteinFinder {

    /**
     * For each protein (UniProt accession) in Reactome, get the top n proteins with the biggest number of pathway/reaction
     * hits difference between different forms of the protein.
     * The form of the protein is specified by a set of annotated translational modifications.
     */

    public static void Main(String args[]) {
        Map<String, Integer> hitDifferences = new HashMap<String,Integer>();

        // Get all proteins from Reactiome
        SchemaService schemaService = ReactomeGraphCore.getService(SchemaService.class);

    }

    /**
     * Gets the sets of reactions that are hit by each form of a protein and gets the maximum nu
     *
     * @param id The UniProt accession for the protein to be checked.
     */
    private static void getReactionHitDifference(String id) {

    }

    /**
     * Get all possible groups of ptms for a specific protein.
     */
    private static Set<Set<Pair<Integer, String>>> getPTMSets() {

        return null;
    }

    /**
     * Get the reaction hits for each protein form.
     *
     * @return
     */
    private static Map<Set<Pair<Integer, String>>, Set<Set<String>>> getReactionHits() {
        return null;
    }

    /**
     * Get list of ewas that are referenced by the same UniProt accession and set of PTMs.
     */
    private static Set<String> getEwasByPTMs() {
        return null;
    }
}
