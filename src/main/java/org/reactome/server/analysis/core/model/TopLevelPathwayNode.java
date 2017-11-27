package org.reactome.server.analysis.core.model;

import com.googlecode.concurrenttrees.radix.node.Node;
import org.reactome.server.analysis.core.model.identifier.Identifier;
import org.reactome.server.analysis.core.model.identifier.MainIdentifier;

import java.util.List;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Luis Francisco Hernández Sánchez <luis.sanchez@uib.no>
 * <p>
 * A node in the pathway hierarchy tree of a species.
 * One node of this type corresponds to one Top Level Pathway for a species.
 * This nodes have children pathways and no parent pathways.
 * They are connectected directly to the hierarchy with the @parentPathwayHierarchy field.
 * This connection preserves the double-linked nature of the tree structure.
 */
public class TopLevelPathwayNode extends PathwayNode {
    private PathwayHierarchy parentPathwayHierarchy;

    public TopLevelPathwayNode(PathwayHierarchy parentPathwayHierarchy, String stId, Long pathwayId, String name, boolean hasDiagram) {
        super(stId, pathwayId, name, hasDiagram);
        this.parentPathwayHierarchy = parentPathwayHierarchy;
    }

    public PathwayHierarchy getParentPathwayHierarchy() {
        return parentPathwayHierarchy;
    }

    @Override
    public SpeciesNode getSpecies() {
        return parentPathwayHierarchy.getSpecies();
    }

    @Override
    public void process(Identifier identifier, MainIdentifier mainIdentifier, Set<AnalysisReaction> reactions) {
        super.process(identifier, mainIdentifier, reactions);
        this.parentPathwayHierarchy.process(identifier, mainIdentifier, reactions);
    }
}
