package org.reactome.server.analysis.core.model;

import org.reactome.server.graph.domain.model.Species;

import java.util.List;
import java.util.Objects;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SpeciesNode {
    private Long speciesID;
    private String taxID;
    private String name;

    protected SpeciesNode(Long speciesID, String taxId, String name) {
        this.speciesID = speciesID;
        this.taxID = taxId;
        this.name = name;
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

    public boolean isHuman(){
        return this.speciesID.equals(SpeciesNodeFactory.HUMAN_DB_ID);
    }

    public boolean isA(Species species){
        return Objects.equals(species.getDbId(), speciesID) || Objects.equals(species.getTaxId(), taxID);
    }

    public boolean isIn(List<Species> speciesList){
        for (Species species : speciesList) {
            if (isA(species)) return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpeciesNode that = (SpeciesNode) o;

        return Objects.equals(speciesID, that.speciesID);
    }

    @Override
    public int hashCode() {
        return speciesID != null ? speciesID.hashCode() : 0;
    }
}
