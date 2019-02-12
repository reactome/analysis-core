package org.reactome.server.analysis.core.result.model;

import org.reactome.server.analysis.core.model.identifier.MainIdentifier;
import org.reactome.server.analysis.core.model.resource.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class MappedIdentifier {

    private String resource;

    private String identifier;

    private List<MappedIdentifier> interactsWith;

    public MappedIdentifier(Resource resource, String identifier, Set<MainIdentifier> interactsWith) {
        this.resource = resource.getName();
        this.identifier = identifier;
        this.interactsWith = new ArrayList<>();
        for (MainIdentifier mainIdentifier : interactsWith) {
            this.interactsWith.add(new MappedIdentifier(mainIdentifier));
        }
    }

    public MappedIdentifier(MainIdentifier identifier){
        this.resource = identifier.getResource().getName();
        this.identifier = identifier.getValue().getId();
    }

    public String getResource() {
        return resource;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<MappedIdentifier> getInteractsWith() {
        return interactsWith;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MappedIdentifier that = (MappedIdentifier) o;
        return Objects.equals(resource, that.resource) &&
                Objects.equals(identifier, that.identifier) &&
                Objects.equals(interactsWith, that.interactsWith);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, identifier, interactsWith);
    }
}
