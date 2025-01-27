package org.reactome.server.analysis.core.importer.query;

import org.neo4j.driver.Record;
import org.reactome.server.analysis.core.model.AnalysisReaction;
import org.reactome.server.analysis.core.util.MapSet;
import org.reactome.server.graph.domain.result.CustomQuery;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class EntitiesQueryResult implements CustomQuery {
    private Long pathway;
    private Long physicalEntity;
    private String speciesName;
    private Long referenceEntity;
    private List<AnalysisReaction> reactions;
    private List<Mod> mods;

    public EntitiesQueryResult() { }

    public Long getPathway() {
        return pathway;
    }

    public void setPathway(Long pathway) {
        this.pathway = pathway;
    }

    public Long getPhysicalEntity() {
        return physicalEntity;
    }

    public void setPhysicalEntity(Long physicalEntity) {
        this.physicalEntity = physicalEntity;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public Long getReferenceEntity() {
        return referenceEntity;
    }

    public void setReferenceEntity(Long referenceEntity) {
        this.referenceEntity = referenceEntity;
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

    public List<Mod> getMods() {
        return mods;
    }

    public void setMods(List<Mod> mods) {
        this.mods = mods;
    }

    @Override
    public CustomQuery build(Record r) {
        EntitiesQueryResult eqr = new EntitiesQueryResult();
        eqr.setPathway(r.get("pathway").asLong(0));
        eqr.setPhysicalEntity(r.get("").asLong(0));
        eqr.setReactions(r.get("reactions").asList(AnalysisReaction::build));
        eqr.setReferenceEntity(r.get("referenceEntity").asLong(0));
        eqr.setSpeciesName(r.get("speciesName").asString(null));
        eqr.setMods(r.get("mods").asList(Mod::build));
        return eqr;
    }
}
