package org.reactome.server.analysis.core.result.model;

import org.reactome.server.analysis.core.model.PathwayNodeData;
import org.reactome.server.analysis.core.model.resource.MainResource;
import org.reactome.server.analysis.core.result.PathwayNodeSummary;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class PathwaySummary {

    private String stId;
    private Long dbId;
    private String name;
    private SpeciesSummary species;
    private boolean llp; //lower level pathway
    private boolean isInDisease;

    private EntityStatistics entities;
    private ReactionStatistics reactions;

    public PathwaySummary(PathwayNodeSummary node, String resource, boolean interactors, boolean importableOnly) {
        this.stId = node.getStId();
        this.dbId = node.getPathwayId();

        this.name = node.getName();
        this.species = new SpeciesSummary(node.getSpecies());
        this.llp = node.isLlp();
        this.isInDisease = node.isInDisease();

        initialize(node.getData(), resource, interactors, importableOnly);
    }

    private void initialize(PathwayNodeData d, String resource, boolean interactors, boolean importableOnly) {
        if (resource.equals("TOTAL")) {
                this.entities = new EntityStatistics(d, interactors, importableOnly);
                this.reactions = new ReactionStatistics(d, importableOnly);
        } else {
            for (MainResource mr : d.getResources()) {
                if (mr.getName().equals(resource)) {
                    this.entities = new EntityStatistics(mr, d, interactors);
                    this.reactions = new ReactionStatistics(mr, d);
                    break;
                }
            }
        }
    }

    public String getStId() {
        return stId;
    }

    public Long getDbId() {
        return dbId;
    }

    public String getName() {
        return name;
    }

    public SpeciesSummary getSpecies() {
        return species;
    }

    public boolean isLlp() {
        return llp;
    }

    public boolean isInDisease() {
        return isInDisease;
    }

    public EntityStatistics getEntities() {
        return entities;
    }

    public ReactionStatistics getReactions() {
        return reactions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PathwaySummary that = (PathwaySummary) o;

        if (dbId != null ? !dbId.equals(that.dbId) : that.dbId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return dbId != null ? dbId.hashCode() : 0;
    }
}
