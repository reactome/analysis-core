package org.reactome.server.analysis.core.result.external;

import org.reactome.server.analysis.core.model.identifier.InteractorIdentifier;
import org.reactome.server.analysis.core.model.identifier.MainIdentifier;

public class ExternalMainIdentifier {

    private String resource;
    private String id;

    public ExternalMainIdentifier() {
    }

    public ExternalMainIdentifier(String resource, String id) {
        this.resource = resource;
        this.id = id;
    }

    public ExternalMainIdentifier(MainIdentifier mainIdentifier) {
        this.resource = mainIdentifier.getResource().getName();
        this.id = mainIdentifier.getValue().getId();
    }

    public ExternalMainIdentifier(String resource, InteractorIdentifier interactor) {
        this.resource = resource;
        this.id = interactor.getMapsTo();
    }

    public String getResource() {
        return resource;
    }

    public String getId() {
        return id;
    }

}
