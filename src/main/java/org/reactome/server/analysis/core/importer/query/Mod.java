package org.reactome.server.analysis.core.importer.query;

import javax.annotation.Nonnull;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class Mod {
    private Long coordinate;
    private String mod;

    public Mod() { }

    public Mod(Long coordinate, String mod) {
        this.coordinate = coordinate;
        this.mod = mod;
    }

    public Long getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Long coordinate) {
        this.coordinate = coordinate;
    }

    public String getMod() {
        return mod;
    }

    public void setMod(@Nonnull String mod) {
        this.mod = mod;
    }
}
