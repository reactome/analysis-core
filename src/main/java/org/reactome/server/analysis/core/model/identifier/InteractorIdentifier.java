package org.reactome.server.analysis.core.model.identifier;

import org.reactome.server.analysis.core.model.AnalysisIdentifier;

import java.util.Objects;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorIdentifier extends AnalysisIdentifier {

    private String mapsTo;

    public InteractorIdentifier(String mapsTo) {
        super(mapsTo);
        this.mapsTo = mapsTo;
    }

    public InteractorIdentifier(AnalysisIdentifier identifier, String mapsTo) {
        this(identifier);
        this.mapsTo = mapsTo;
    }

    public InteractorIdentifier(AnalysisIdentifier identifier) {
        super(identifier.getId(), identifier.getExp());
    }

    public String getMapsTo() {
        return mapsTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InteractorIdentifier that = (InteractorIdentifier) o;
        return Objects.equals(mapsTo, that.mapsTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), mapsTo);
    }
}
