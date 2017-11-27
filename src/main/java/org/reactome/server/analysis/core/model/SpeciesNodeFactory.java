package org.reactome.server.analysis.core.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Luis Francisco Hernández Sánchez <luis.sanchez@uib.no>
 */
public abstract class SpeciesNodeFactory {

    public static long HUMAN_DB_ID = 48887L;
    public static String HUMAN_TAX_ID = "9606";
    public static String HUMAN_STR = "Homo sapiens";

    private static Map<Long, SpeciesNode> speciesMap = new HashMap<Long, SpeciesNode>();

    public static SpeciesNode getSpeciesNode(Long speciesID, String taxID, String name) {
        SpeciesNode speciesNode = speciesMap.get(speciesID);
        if (speciesNode == null) {
            speciesNode = new SpeciesNode(speciesID, taxID, name);
            speciesMap.put(speciesID, speciesNode);
        }
        return speciesNode;
    }

    public static SpeciesNode getHumanNode() {
        return new SpeciesNode(HUMAN_DB_ID, HUMAN_TAX_ID, HUMAN_STR);
    }
}
