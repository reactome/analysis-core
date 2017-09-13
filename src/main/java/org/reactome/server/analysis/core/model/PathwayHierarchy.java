package org.reactome.server.analysis.core.model;

import org.reactome.server.analysis.core.model.identifier.Identifier;
import org.reactome.server.analysis.core.model.identifier.InteractorIdentifier;
import org.reactome.server.analysis.core.model.identifier.MainIdentifier;
import org.reactome.server.graph.domain.model.TopLevelPathway;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class PathwayHierarchy implements Serializable {
    private Set<PathwayRoot> children;
    private SpeciesNode species;

    private PathwayNodeData data;

    public PathwayHierarchy(SpeciesNode species) {
        this.species = species;
        this.children = new HashSet<>();
        this.data = new PathwayNodeData();
    }

    public PathwayNode addTopLevelPathway(TopLevelPathway tlp){
        PathwayRoot node = new PathwayRoot(this, tlp.getStId(), tlp.getDbId(), tlp.getDisplayName(), tlp.getHasDiagram());
        this.children.add(node);
        return node;
    }

    public Set<PathwayRoot> getChildren() {
        return children;
    }

    public PathwayNodeData getData() {
        return data;
    }

    public SpeciesNode getSpecies() {
        return species;
    }

    protected Set<PathwayNode> getHitPathways(){
        Set<PathwayNode> rtn = new HashSet<>();
        for (PathwayNode node : this.children) {
            rtn.addAll(node.getHitNodes());
        }
        return rtn;
    }

    public void setCountersAndCleanUp(){
        this.data.setCounters(getData());
        for (PathwayRoot node : children) {
            node.setCounters(getData());
        }
    }

    public void process(Identifier identifier, MainIdentifier mainIdentifier, Set<AnalysisReaction> reactions){
        this.data.addEntity(identifier, mainIdentifier);
        this.data.addReactions(mainIdentifier.getResource(), reactions);
    }

    public void processInteractor(InteractorIdentifier identifier, MainIdentifier mainIdentifier, Set<AnalysisReaction> reactions){
        this.data.addInteractors(mainIdentifier, identifier);
        this.data.addReactions(mainIdentifier.getResource(), reactions);
    }
}
