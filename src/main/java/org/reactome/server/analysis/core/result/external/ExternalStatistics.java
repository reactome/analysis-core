package org.reactome.server.analysis.core.result.external;

import org.reactome.server.analysis.core.model.PathwayNodeData;
import org.reactome.server.analysis.core.model.resource.MainResource;

import java.util.List;

public class ExternalStatistics {
    private String resource;

    private Integer entitiesCount;
    private Integer entitiesFound;
    private Double entitiesRatio;

    private Double entitiesPValue;
    private Double entitiesFDR;

    private Integer interactorsCount;
    private Integer interactorsFound;
    private Double interactorsRatio;

    private Integer entitiesAndInteractorsCount;
    private Integer entitiesAndInteractorsFound;

    private Integer reactionsCount;
    private Integer reactionsFound;
    private Double reactionsRatio;

    private List<Double> exp;

    public ExternalStatistics() {
    }
    ExternalStatistics(PathwayNodeData data) {
        this(data, false);
    }

    ExternalStatistics(PathwayNodeData data, boolean importableOnly) {
        this.resource = "TOTAL";
        this.entitiesCount = data.getEntitiesCount(importableOnly);
        this.entitiesFound = data.getEntitiesFound(importableOnly);
        this.entitiesRatio = data.getEntitiesRatio(importableOnly);
        this.entitiesPValue = data.getEntitiesPValue(importableOnly);
        this.entitiesFDR = data.getEntitiesFDR(importableOnly);
        this.interactorsCount = data.getInteractorsCount(importableOnly);
        this.interactorsFound = data.getInteractorsFound(importableOnly);
        this.interactorsRatio = data.getInteractorsRatio(importableOnly);
        this.entitiesAndInteractorsCount = data.getEntitiesAndInteractorsCount(importableOnly);
        this.entitiesAndInteractorsFound = data.getEntitiesAndInteractorsFound(importableOnly);
        this.reactionsCount = data.getReactionsCount(importableOnly);
        this.reactionsFound = data.getReactionsFound(importableOnly);
        this.reactionsRatio = data.getReactionsRatio(importableOnly);
        this.exp = data.getExpressionValuesAvg(importableOnly);
    }
    ExternalStatistics(PathwayNodeData data, MainResource mr) {
        this.resource = mr.getName();
        this.entitiesCount = data.getEntitiesCount(mr);
        this.entitiesFound = data.getEntitiesFound(mr);
        this.entitiesRatio = data.getEntitiesRatio(mr);
        this.entitiesPValue = data.getEntitiesPValue(mr);
        this.entitiesFDR = data.getEntitiesFDR(mr);
        this.interactorsCount = data.getInteractorsCount(mr);
        this.interactorsFound = data.getInteractorsFound(mr);
        this.interactorsRatio = data.getInteractorsRatio(mr);
        this.entitiesAndInteractorsCount = data.getEntitiesAndInteractorsCount(mr);
        this.entitiesAndInteractorsFound = data.getEntitiesAndInteractorsFound(mr);
        this.reactionsCount = data.getReactionsCount(mr);
        this.reactionsFound = data.getReactionsFound(mr);
        this.reactionsRatio = data.getReactionsRatio(mr);
        this.exp = data.getExpressionValuesAvg(mr);
    }

    public String getResource() {
        return resource;
    }

    public Integer getEntitiesCount() {
        return entitiesCount;
    }

    public Integer getEntitiesFound() {
        return entitiesFound;
    }

    public Double getEntitiesRatio() {
        return entitiesRatio;
    }

    public Double getEntitiesPValue() {
        return entitiesPValue;
    }

    public Double getEntitiesFDR() {
        return entitiesFDR;
    }

    public Integer getInteractorsCount() {
        return interactorsCount;
    }

    public Integer getInteractorsFound() {
        return interactorsFound;
    }

    public Double getInteractorsRatio() {
        return interactorsRatio;
    }

    public Integer getEntitiesAndInteractorsCount() {
        return entitiesAndInteractorsCount;
    }

    public Integer getEntitiesAndInteractorsFound() {
        return entitiesAndInteractorsFound;
    }

    public Integer getReactionsCount() {
        return reactionsCount;
    }

    public Integer getReactionsFound() {
        return reactionsFound;
    }

    public Double getReactionsRatio() {
        return reactionsRatio;
    }

    public List<Double> getExp() {
        return exp;
    }
}
