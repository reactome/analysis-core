package org.reactome.server.analysis.core.result.external;

import org.reactome.server.analysis.core.model.AnalysisIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalIdentifier that = (ExternalIdentifier) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
