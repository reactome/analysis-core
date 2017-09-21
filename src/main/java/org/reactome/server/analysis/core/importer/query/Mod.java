package org.reactome.server.analysis.core.importer.query;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class Mod {
    Long coordinate;
    String mod;

    public Mod(Long coordinate, String mod) {
        this.coordinate = coordinate;
        this.mod = mod;
    }

    public Long getCoordinate() {
        return coordinate;
    }

    public String getMod() {
        return mod;
    }
}
