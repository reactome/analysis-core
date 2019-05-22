package org.reactome.server.analysis.core.result.model;

/**
 * Minimum amount of information per pathway related to an analysis result
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class PathwayBase {

    private String stId;
    private Long dbId;
    private Boolean isDisease;
    private EntityStatistics entities;

    public PathwayBase(PathwaySummary pathwaySummary) {
        this.stId = pathwaySummary.getStId();
        this.dbId = pathwaySummary.getDbId();
        this.isDisease = pathwaySummary.isInDisease();
        this.entities = pathwaySummary.getEntities();
    }

    public String getStId() {
        return stId;
    }

    public Long getDbId() {
        return dbId;
    }

    public Boolean getDisease() {
        return isDisease;
    }

    public EntityStatistics getEntities() {
        return entities;
    }
}
