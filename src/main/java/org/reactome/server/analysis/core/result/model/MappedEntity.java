package org.reactome.server.analysis.core.result.model;

import java.util.Set;

public class MappedEntity {

    private String identifier;

    private Set<MappedIdentifier> mapsTo;

    public MappedEntity(String identifier, Set<MappedIdentifier> mapsTo) {
        this.identifier = identifier;
        this.mapsTo = mapsTo;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Set<MappedIdentifier> getMapsTo() {
        return mapsTo;
    }
}
