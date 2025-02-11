package org.reactome.server.analysis.core.model;

import org.reactome.server.analysis.core.model.identifier.Identifier;
import org.reactome.server.analysis.core.model.identifier.InteractorIdentifier;
import org.reactome.server.analysis.core.model.identifier.MainIdentifier;

import java.util.Set;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class PathwayRoot extends PathwayNode {
    private PathwayHierarchy pathwayHierarchy;

    public PathwayRoot(PathwayHierarchy pathwayHierarchy, String stId, Long pathwayId, String name, boolean hasDiagram, boolean inDisease) {
        super(stId, pathwayId, name, hasDiagram, inDisease);
        this.pathwayHierarchy = pathwayHierarchy;
    }

    public PathwayHierarchy getPathwayHierarchy() {
        return pathwayHierarchy;
    }

    @Override
    public SpeciesNode getSpecies() {
        return pathwayHierarchy.getSpecies();
    }

    @Override
    public void process(Identifier identifier, MainIdentifier mainIdentifier, Set<AnalysisReaction> reactions){
        super.process(identifier, mainIdentifier, reactions);
        this.pathwayHierarchy.process(identifier, mainIdentifier, reactions);
    }

    @Override
    public void processInteractor(InteractorIdentifier identifier, MainIdentifier mainIdentifier, Set<AnalysisReaction> reactions) {
        super.processInteractor(identifier, mainIdentifier, reactions);
        this.pathwayHierarchy.processInteractor(identifier, mainIdentifier, reactions);
    }
}
