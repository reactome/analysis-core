package org.reactome.server.analysis.core.importer.query;

import org.reactome.server.analysis.core.model.AnalysisReaction;
import org.reactome.server.analysis.core.util.MapSet;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsTargetQueryResult {

    private String databaseName;
    private String identifier;

    private Long pathway;
    private List<AnalysisReaction> reactions;

    public InteractorsTargetQueryResult() { }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Long getPathway() {
        return pathway;
    }

    public void setPathway(Long pathway) {
        this.pathway = pathway;
    }

    public List<AnalysisReaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<AnalysisReaction> reactions) {
        this.reactions = reactions;
    }

    public MapSet<Long, AnalysisReaction> getPathwayReactions(){
        MapSet<Long, AnalysisReaction> rtn = new MapSet<>();
        rtn.add(getPathway(), reactions);
        return rtn;
    }
}
