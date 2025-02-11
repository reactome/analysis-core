package org.reactome.server.analysis.core.result.model;

import org.reactome.server.analysis.core.model.PathwayNodeData;
import org.reactome.server.analysis.core.model.resource.MainResource;

import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
//@ApiModel(value = "EntityStatistics", description = "Statistics for an entity type")
public class EntityStatistics extends Statistics {
    private Integer curatedTotal;
    private Integer curatedFound;

    private Integer interactorsTotal;
    private Integer interactorsFound;

    private Double pValue;
    private Double fdr;
    private List<Double> exp = null;


    public EntityStatistics(PathwayNodeData d, boolean interactors, boolean importableOnly) {
        super("TOTAL",
                interactors ? d.getEntitiesAndInteractorsCount(importableOnly) : d.getEntitiesCount(importableOnly),
                interactors ? d.getEntitiesAndInteractorsFound(importableOnly) : d.getEntitiesFound(importableOnly),
                interactors ? d.getInteractorsRatio(importableOnly) : d.getEntitiesRatio(importableOnly));
        this.fdr = d.getEntitiesFDR(importableOnly);
        this.pValue = d.getEntitiesPValue(importableOnly);
        this.exp = d.getExpressionValuesAvg(importableOnly);
        if (interactors) {
            this.curatedFound = d.getEntitiesFound();
            this.curatedTotal = d.getEntitiesCount();
            this.interactorsFound = d.getInteractorsFound();
            this.interactorsTotal = d.getInteractorsCount();
        }
    }

    public EntityStatistics(MainResource resource, PathwayNodeData d, boolean interactors) {
        super(resource.getName(),
                interactors ? d.getEntitiesAndInteractorsCount(resource) : d.getEntitiesCount(resource),
                interactors ? d.getEntitiesAndInteractorsFound(resource) : d.getEntitiesFound(resource),
                interactors ? d.getInteractorsRatio(resource) : d.getEntitiesRatio(resource));
        this.fdr = d.getEntitiesFDR(resource);
        this.pValue = d.getEntitiesPValue(resource);
        this.exp = d.getExpressionValuesAvg(resource);
        if (interactors) {
            this.curatedFound = d.getEntitiesFound(resource);
            this.curatedTotal = d.getEntitiesCount(resource);
            this.interactorsFound = d.getInteractorsFound(resource);
            this.interactorsTotal = d.getInteractorsCount(resource);
        }
    }

    public Integer getCuratedTotal() {
        return curatedTotal;
    }

    public Integer getCuratedFound() {
        return curatedFound;
    }

    public Integer getInteractorsTotal() {
        return interactorsTotal;
    }

    public Integer getInteractorsFound() {
        return interactorsFound;
    }

    public Double getpValue() {
        return pValue;
    }

    public Double getFdr() {
        return fdr;
    }

    public List<Double> getExp() {
        return exp;
    }
}
