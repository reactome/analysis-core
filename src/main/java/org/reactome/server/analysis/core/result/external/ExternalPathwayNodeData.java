package org.reactome.server.analysis.core.result.external;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.reactome.server.analysis.core.model.PathwayNodeData;
import org.reactome.server.analysis.core.model.resource.MainResource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExternalPathwayNodeData {

    private List<ExternalIdentifier> entities;
    private List<ExternalInteractor> interactors;
    private List<ExternalAnalysisReaction> reactions;
    private List<ExternalStatistics> statistics;

    public ExternalPathwayNodeData() {
    }

    ExternalPathwayNodeData(PathwayNodeData data, boolean importableOnly) {
        this.statistics = new ArrayList<>();

        //if (data.getEntitiesAndInteractorsFound() > 0) {
        if (data.getEntitiesPValue() != null) {
            this.statistics.add(new ExternalStatistics(data, importableOnly));
            for (MainResource mr : data.getResources()) {
                if (data.getEntitiesPValue(mr) != null) {
                    if (!importableOnly || !mr.isAuxMainResource()) {
                        this.statistics.add(new ExternalStatistics(data, mr));
                    }
                }
                //if (data.getEntitiesAndInteractorsFound(mr) > 0)
            }
        }

        this.entities = data.getExternalEntities(importableOnly);
        this.interactors = data.getExternalInteractors(importableOnly);
        this.reactions = data.getExternalReactions(importableOnly);
    }

    public List<ExternalIdentifier> getEntities() {
        return entities;
    }

    public List<ExternalInteractor> getInteractors() {
        return interactors;
    }

    public List<ExternalAnalysisReaction> getReactions() {
        return reactions;
    }

    public List<ExternalStatistics> getStatistics() {
        return statistics;
    }

    @JsonIgnore
    public Set<String> getResources() {
        Set<String> resources = new HashSet<>();
        if (entities != null) {
            for (ExternalIdentifier entity : entities) {
                for (ExternalMainIdentifier emi : entity.getMapsTo()) {
                    resources.add(emi.getResource());
                }
            }
        }
        if (interactors != null) {
            for (ExternalInteractor interactor : interactors) {
                for (ExternalInteraction externalInteraction : interactor.getMapsTo()) {
                    for (ExternalMainIdentifier emi : externalInteraction.getInteractsWith()) {
                        resources.add(emi.getResource());
                    }
                }
            }
        }
        return resources;
    }
}
