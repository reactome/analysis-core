package org.reactome.server.analysis.core.model;

public enum AnalysisType {
    SPECIES_COMPARISON,
    OVERREPRESENTATION,
    EXPRESSION,
    GSA_REGULATION, //DISCRETE values in the "expression" for pathways (proteins will contain normal expression values)
    GSA_STATISTICS,
    GSVA;

    public static AnalysisType getType(String type){
        for (AnalysisType t : values()) {
            if(t.toString().toLowerCase().equals(type.toLowerCase())){
                return t;
            }
        }
        return null;
    }
}
