package org.reactome.server.analysis.core.model;

import org.reactome.server.analysis.core.model.identifier.MainIdentifier;
import org.reactome.server.analysis.core.model.resource.MainResource;
import org.reactome.server.analysis.core.util.MapSet;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class EntityNode {

    private MainIdentifier identifier = null;
    private List<Modification> modifications = null;

    private SpeciesNode species;

    private Map<SpeciesNode, EntityNode> inferredFrom;
    private Map<SpeciesNode, EntityNode> inferredTo;

    //We DO NOT use PathwayNode here because at some point cloning the hierarchies
    //structure will be needed and keeping this separate will help to maintain the
    //links between both structures easy through the pathway location map
    private MapSet<Long, AnalysisReaction> pathwayReactions = null;

    public EntityNode(SpeciesNode species, MainResource mainResource, String mainIdentifier, List<Modification> modifications) {
        this.species = species;
        this.identifier = new MainIdentifier(mainResource, new AnalysisIdentifier(mainIdentifier));
        this.modifications = modifications;
        Collections.sort(this.modifications);
    }

    public void addInferredTo(EntityNode inferredTo) {
        //We do NOT want cycles at this point
        EntityNode aux = inferredTo.getInferredFrom().get(this.species);
        if (aux != null && aux.equals(this)) return;

        if (this.inferredTo == null) {
            this.inferredTo = new HashMap<>();
        }
        this.inferredTo.put(inferredTo.getSpecies(), inferredTo);
    }

    public void addPathwayReactions(MapSet<Long, AnalysisReaction> pathwayReactions) {
        if (this.pathwayReactions == null) {
            this.pathwayReactions = new MapSet<>();
        }
        this.pathwayReactions.addAll(pathwayReactions);
    }


    public EntityNode getProjection(SpeciesNode species) {
        if (this.species == null)
            return this; //DO NOT CHANGE THIS AGAIN. If is a SmallMolecule it does NOT have species..
        if (this.species.equals(species)) return this;
        EntityNode rtn = null;
        if (inferredFrom != null) {
            rtn = inferredFrom.get(species);
        }
        if (rtn == null && inferredTo != null) {
            rtn = inferredTo.get(species);
        }
        return rtn;
    }

    public MainIdentifier getIdentifier() {
        return identifier;
    }

    public List<Modification> getModifications() {
        return modifications;
    }

    public Map<SpeciesNode, EntityNode> getInferredFrom() {
        if (inferredFrom == null) return new HashMap<>();
        return inferredFrom;
    }

    public Map<SpeciesNode, EntityNode> getInferredTo() {
        if (inferredTo == null) return new HashMap<>();
        return inferredTo;
    }

    public Set<AnalysisReaction> getReactions(Long pathwayId) {
        Set<AnalysisReaction> rtn = new HashSet<>();
        if (this.pathwayReactions != null) {
            Set<AnalysisReaction> reactions = this.pathwayReactions.getElements(pathwayId);
            if (reactions != null) {
                rtn.addAll(reactions);
            }
        }
        return rtn;
    }

    public SpeciesNode getSpecies() {
        return species;
    }

    public Set<Long> getPathwayIds() {
        Set<Long> rtn = new HashSet<>();
        if (this.pathwayReactions != null) {
            rtn.addAll(this.pathwayReactions.keySet());
        }
        return rtn;
    }

    public MapSet<Long, AnalysisReaction> getPathwayReactions() {
        return pathwayReactions;
    }

    public boolean isDirectlyInADiagram() {
        return pathwayReactions != null && !pathwayReactions.keySet().isEmpty();
    }

    protected void setOrthologiesCrossLinks() {
        for (SpeciesNode speciesNode : this.getInferredTo().keySet()) {
            if (this.inferredTo.get(speciesNode).inferredFrom == null) {
                this.inferredTo.get(speciesNode).inferredFrom = new HashMap<>();
            }
            this.inferredTo.get(speciesNode).inferredFrom.put(this.species, this);
        }
        for (SpeciesNode speciesNode : this.getInferredFrom().keySet()) {
            if (this.inferredFrom.get(speciesNode).inferredTo == null) {
                this.inferredFrom.get(speciesNode).inferredTo = new HashMap<>();
            }
            this.inferredFrom.get(speciesNode).inferredTo.put(this.species, this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityNode that = (EntityNode) o;

        if (identifier != null ? !identifier.equals(that.identifier) : that.identifier != null) return false;
        return modifications != null ? modifications.equals(that.modifications) : that.modifications == null;
    }

    @Override
    public int hashCode() {
        int result = identifier != null ? identifier.hashCode() : 0;
        result = 31 * result + (modifications != null ? modifications.hashCode() : 0);
        return result;
    }
}
