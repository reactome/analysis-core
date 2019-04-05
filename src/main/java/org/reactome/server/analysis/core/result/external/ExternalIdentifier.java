package org.reactome.server.analysis.core.result.external;

import org.reactome.server.analysis.core.model.AnalysisIdentifier;

import java.util.ArrayList;
import java.util.List;

public class ExternalIdentifier {

    private String id;
    private List<Double> exp;
    private List<ExternalMainIdentifier> mapsTo;

    public ExternalIdentifier() {
    }

    public ExternalIdentifier(AnalysisIdentifier identifier) {
        this.id = identifier.getId();
        this.exp = new ArrayList<>(identifier.getExp());
    }

    public void addMapsTo(ExternalMainIdentifier mainIdentifier) {
        if (mapsTo == null) mapsTo = new ArrayList<>();
        mapsTo.add(mainIdentifier);
    }

    public String getId() {
        return id;
    }

    public List<ExternalMainIdentifier> getMapsTo() {
        return mapsTo;
    }

    public List<Double> getExp() {
        return exp;
    }
}
