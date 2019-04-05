package org.reactome.server.analysis.core.result.external;

import org.reactome.server.analysis.core.model.identifier.InteractorIdentifier;

import java.util.ArrayList;
import java.util.List;

public class ExternalInteractor {

    private String id;
    private List<Double> exp;
    private List<ExternalInteraction> mapsTo;

    public ExternalInteractor() {
    }

    public ExternalInteractor(InteractorIdentifier interactor) {
        this.id = interactor.getId();
        this.exp = interactor.getExp();
        this.mapsTo = new ArrayList<>();
    }

    public void addMapsTo(ExternalInteraction interaction){
        this.mapsTo.add(interaction);
    }

    public String getId() {
        return id;
    }

    public List<Double> getExp() {
        return exp;
    }

    public List<ExternalInteraction> getMapsTo() {
        return mapsTo;
    }
}
