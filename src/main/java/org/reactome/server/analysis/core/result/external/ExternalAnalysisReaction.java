package org.reactome.server.analysis.core.result.external;

import org.reactome.server.analysis.core.model.AnalysisReaction;

import java.util.ArrayList;
import java.util.List;

public class ExternalAnalysisReaction {

    private List<String> resources;
    private Long dbId;
    private String stId;

    public ExternalAnalysisReaction() {
    }

    public ExternalAnalysisReaction(AnalysisReaction reaction) {
        this.dbId = reaction.getDbId();
        this.stId = reaction.getStId();
        this.resources = new ArrayList<>();
    }

    public void add(String resource) {
        this.resources.add(resource);
    }

    public List<String> getResources() {
        return resources;
    }

    public Long getDbId() {
        return dbId;
    }

    public String getStId() {
        return stId;
    }
}
