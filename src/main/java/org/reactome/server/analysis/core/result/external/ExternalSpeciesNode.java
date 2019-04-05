package org.reactome.server.analysis.core.result.external;

import org.reactome.server.analysis.core.model.SpeciesNode;

public class ExternalSpeciesNode {

    private Long speciesID;
    private String taxID;
    private String name;

    public ExternalSpeciesNode() {
    }

    ExternalSpeciesNode(SpeciesNode species) {
        this.speciesID = species.getSpeciesID();
        this.taxID = species.getTaxID();
        this.name = species.getName();
    }

    public Long getSpeciesID() {
        return speciesID;
    }

    public String getTaxID() {
        return taxID;
    }

    public String getName() {
        return name;
    }
}
