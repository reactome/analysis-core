package org.reactome.server.analysis.core.model;

import org.reactome.server.analysis.core.model.identifier.Identifier;
import org.reactome.server.analysis.core.model.identifier.MainIdentifier;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * <p>
 * The PathwayHierarchy structure for a specific species. The structure to organize the pathways is a set of double-linked
 * trees represented by a list of Top Level Pathway nodes that point to their children pathways respectively.
 */
public class PathwayHierarchy implements Serializable {

    private SpeciesNode species;                                                                                        // There is one hierarchy for each species
    private Set<TopLevelPathwayNode> children;                                                                          // The list of Top Level Pathways for this species
    private PathwayNodeData data;                                                                                       // Contains results of the analysis

    /**
     * Class constructor which fills only the species data. The list of children pathways and the analysis data remain empty.
     */
    public PathwayHierarchy(SpeciesNode species) {
        this.species = species;
        this.children = new HashSet<TopLevelPathwayNode>();
        this.data = new PathwayNodeData();
    }

    /**
     * Adds one TopLevelpathwayNode to the list of children nodes of the Hierarchy.
     *
     * @param stId
     * @param pathwayId
     * @param name
     * @param hasDiagram
     * @return
     */
    public PathwayNode addTopLevelPathway(String stId, Long pathwayId, String name, boolean hasDiagram) {
        TopLevelPathwayNode node = new TopLevelPathwayNode(this, stId, pathwayId, name, hasDiagram);
        this.children.add(node);
        return node;
    }

    public Set<TopLevelPathwayNode> getChildren() {
        return children;
    }

    public PathwayNodeData getData() {
        return data;
    }

    public SpeciesNode getSpecies() {
        return species;
    }

    protected Set<PathwayNode> getHitPathways() {
        Set<PathwayNode> rtn = new HashSet<>();
        for (PathwayNode node : this.children) {
            rtn.addAll(node.getHitNodes());
        }
        return rtn;
    }

    public void setCountersAndCleanUp() {
        this.data.setCounters(getData());
        for (TopLevelPathwayNode node : children) {
            node.setCounters(getData());
        }
    }

    public void process(Identifier identifier, MainIdentifier mainIdentifier, Set<AnalysisReaction> reactions) {
        this.data.addEntity(identifier, mainIdentifier);
        this.data.addReactions(mainIdentifier.getResource(), reactions);
    }
}
