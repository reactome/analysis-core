package org.reactome.server.analysis.core.model;

import org.reactome.server.analysis.core.result.external.ExternalSpeciesNode;
import org.reactome.server.graph.domain.model.Species;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class SpeciesNodeFactory {

    public static long HUMAN_DB_ID = 48887L;
    public static String HUMAN_TAX_ID = "9606";
    public static String HUMAN_STR = "Homo sapiens";

    private static Map<Long, SpeciesNode> speciesMap = new HashMap<>();

    public static SpeciesNode getSpeciesNode(Long speciesID, String taxID, String name) {
        SpeciesNode speciesNode = speciesMap.get(speciesID);
        if (speciesNode == null) {
            speciesNode = new SpeciesNode(speciesID, taxID, name);
            speciesMap.put(speciesID, speciesNode);
        }
        return speciesNode;
    }

    public static SpeciesNode getSpeciesNode(Species s) {
        SpeciesNode speciesNode = speciesMap.get(s.getDbId());
        if (speciesNode == null) {
            try {
                speciesNode = new SpeciesNode(s.getDbId(), s.getTaxId(), s.getDisplayName());
                speciesMap.put(s.getDbId(), speciesNode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return speciesNode;
    }

    public static SpeciesNode getSpeciesNode(ExternalSpeciesNode species) {
        return getSpeciesNode(species.getSpeciesID(), species.getTaxID(), species.getName());
    }

    public static SpeciesNode getHumanNode() {
        return new SpeciesNode(HUMAN_DB_ID, HUMAN_TAX_ID, HUMAN_STR);
    }
}
