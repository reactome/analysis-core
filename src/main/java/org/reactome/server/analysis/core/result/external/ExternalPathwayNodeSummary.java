package org.reactome.server.analysis.core.result.external;

import org.reactome.server.analysis.core.result.PathwayNodeSummary;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ExternalPathwayNodeSummary {

    private String stId;
    private Long dbId;
    private String name;
    private ExternalSpeciesNode species;
    private Boolean llp;
    private Boolean inDisease;
    private ExternalPathwayNodeData data;

    public ExternalPathwayNodeSummary() {
    }

    ExternalPathwayNodeSummary(PathwayNodeSummary pns, boolean importableOnly) {
        this.stId = pns.getStId();
        this.dbId = pns.getPathwayId();
        this.name = pns.getName();
        this.species = new ExternalSpeciesNode(pns.getSpecies());
        this.llp = pns.isLlp();
        this.inDisease = pns.isInDisease();
        this.data = new ExternalPathwayNodeData(pns.getData(), importableOnly);
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

    public ExternalSpeciesNode getSpecies() {
        return species;
    }

    public Boolean isLlp() {
        return llp;
    }

    public Boolean isInDisease() {
        return inDisease;
    }

    public ExternalPathwayNodeData getData() {
        return data;
    }
}
