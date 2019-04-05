package org.reactome.server.analysis.core.result.external;

import java.util.ArrayList;
import java.util.List;

public class ExternalInteraction {

    private String id;
    private List<ExternalMainIdentifier> interactsWith;

    public ExternalInteraction() {
    }

    public ExternalInteraction(String id) {
        this.id = id;
        this.interactsWith = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void addExternalMainIdentifier(ExternalMainIdentifier mi){
        this.interactsWith.add(mi);
    }

    public List<ExternalMainIdentifier> getInteractsWith() {
        return interactsWith;
    }
}
