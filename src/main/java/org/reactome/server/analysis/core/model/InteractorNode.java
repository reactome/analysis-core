package org.reactome.server.analysis.core.model;

import org.reactome.server.analysis.core.model.identifier.MainIdentifier;
import org.reactome.server.analysis.core.util.MapSet;

import java.util.HashSet;
import java.util.Set;

/**
 * Interactors are not longer connected to a EntityNode. They are treated as main objects in the intermediate
 * data structure.
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorNode {

    private String accession;

    private Set<MainIdentifier> interactsWith;

    //We DO NOT use PathwayNode here because at some point cloning the hierarchies
    //structure will be needed and keeping this separate will help to maintain the
    //links between both structures easy through the pathway location map
    private MapSet<Long, AnalysisReaction> pathwayReactions = null;

    public InteractorNode(String accession) {
        this.interactsWith = new HashSet<>();
        this.accession = accession;
    }

    public void addInteractsWith(MainIdentifier interactsWith) {
        this.interactsWith.add(interactsWith);
    }

    public String getAccession() {
        return accession;
    }

    public Set<MainIdentifier> getInteractsWith() {
        return interactsWith;
    }

    public MapSet<Long, AnalysisReaction> getPathwayReactions() {
        return pathwayReactions;
    }

    public void addPathwayReactions(MapSet<Long, AnalysisReaction> pathwayReactions) {
        if (this.pathwayReactions == null) {
            this.pathwayReactions = new MapSet<>();
        }
        this.pathwayReactions.addAll(pathwayReactions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InteractorNode that = (InteractorNode) o;

        return accession != null ? accession.equals(that.accession) : that.accession == null;

    }

    @Override
    public int hashCode() {
        return accession != null ? accession.hashCode() : 0;
    }
}
