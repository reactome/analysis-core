package org.reactome.server.analysis.core.model;

public enum AnalysisType {
    SPECIES_COMPARISON,
    OVERREPRESENTATION,
    EXPRESSION;

    public static AnalysisType getType(String type){
        for (AnalysisType t : values()) {
            if(t.toString().toLowerCase().equals(type.toLowerCase())){
                return t;
            }
        }
        return null;
    }
}
