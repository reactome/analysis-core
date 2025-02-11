package org.reactome.server.analysis.core.result;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public enum AnalysisSortType {
    NAME,
    TOTAL_ENTITIES,
    TOTAL_INTERACTORS,
    TOTAL_REACTIONS,
    FOUND_ENTITIES,
    FOUND_INTERACTORS,
    FOUND_REACTIONS,
    ENTITIES_RATIO,
    ENTITIES_PVALUE,
    ENTITIES_FDR,
    REACTIONS_RATIO;

    public static AnalysisSortType getSortType(String type){
        if(type!=null){
            for (AnalysisSortType sortType : values()) {
                if(sortType.toString().equals(type.toUpperCase())){
                    return sortType;
                }
            }
        }
        return ENTITIES_PVALUE;
    }
}
