package org.reactome.server.analysis.core.importer.query;

import org.reactome.server.analysis.core.importer.EntitiesBuilder;
import org.reactome.server.analysis.core.model.AnalysisReaction;

import java.util.ArrayList;
import java.util.List;

public class EntitiesQueryResult {
    private Long pathway;
    private Long physicalEntity;

    private Long referenceEntity;
    private String identifier;
    private String variantIdentifier;

    private List<String> reactions;
    private List<String> mods;

    public EntitiesQueryResult() {
    }

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

    public Long getReferenceEntity() {
        return referenceEntity;
    }

    public void setReferenceEntity(Long referenceEntity) {
        this.referenceEntity = referenceEntity;
    }

    public String getIdentifier() {
        return variantIdentifier!=null && !variantIdentifier.isEmpty() ? variantIdentifier : identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setVariantIdentifier(String variantIdentifier) {
        this.variantIdentifier = variantIdentifier;
    }

    public List<AnalysisReaction> getReactions() {
        List<AnalysisReaction> rtn = new ArrayList<>();
        for (String reaction : reactions) {
            String[] aux = reaction.split(EntitiesBuilder.splitter);
            rtn.add(new AnalysisReaction(Long.valueOf(aux[0]), aux[1]));
        }
        return rtn;
    }

    public void setReactions(List<String> reactions) {
        this.reactions = reactions;
    }

    public List<Mod> getMods() {
        List<Mod> rtn = new ArrayList<>();
        for (String mod : mods) {
            String[] aux = mod.split(EntitiesBuilder.splitter);
            Long coordinate = null;
            try {
                coordinate = Long.valueOf(aux[0]);
            } catch (NumberFormatException e){
                //Nothing here
            }
            rtn.add(new Mod(coordinate, aux[1]));
        }
        return rtn;
    }

    public void setMods(List<String> mods) {
        this.mods = mods;
    }
}
