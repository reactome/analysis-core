package org.reactome.server.analysis.core.result.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.reactome.server.analysis.core.model.SpeciesNode;
import org.reactome.server.analysis.core.model.SpeciesNodeFactory;
import org.reactome.server.analysis.core.result.external.ExternalSpeciesNode;

import java.util.Objects;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SpeciesSummary implements Comparable<SpeciesSummary> {
    private Long dbId;
    private String taxId;
    private String name;
    private Integer pathways;
    private Integer filtered;

    public SpeciesSummary(SpeciesNode species) {
        this.dbId = species.getSpeciesID();
        this.taxId = species.getTaxID();
        this.name = species.getName();
    }

    public SpeciesSummary(ExternalSpeciesNode species) {
        this.dbId = species.getSpeciesID();
        this.taxId = species.getTaxID();
        this.name = species.getName();
    }

    public SpeciesSummary(SpeciesNode species, Integer pathways) {
        this(species);
        this.pathways = pathways;
        this.filtered = pathways;
    }

    public Long getDbId() {
        return dbId;
    }

    public String getTaxId() {
        return taxId;
    }

    public String getName() {
        return name;
    }

    public Integer getPathways() {
        return pathways;
    }

    public Integer getFiltered() {
        return filtered;
    }

    public void setFiltered(Integer filtered) {
        this.filtered = filtered;
    }

    @JsonIgnore
    public SpeciesNode getSpeciesNode(){
        return SpeciesNodeFactory.getSpeciesNode(dbId, taxId, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpeciesSummary that = (SpeciesSummary) o;
        return Objects.equals(dbId, that.dbId) &&
                Objects.equals(taxId, that.taxId) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbId, taxId, name);
    }

    @Override
    public int compareTo(SpeciesSummary o) {
        return pathways.compareTo(o.pathways);
    }
}
