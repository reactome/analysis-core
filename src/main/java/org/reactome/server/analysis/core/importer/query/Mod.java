package org.reactome.server.analysis.core.importer.query;

import org.neo4j.driver.Value;
import org.springframework.lang.NonNull;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
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

    public void setMod(@NonNull String mod) {
        this.mod = mod;
    }

    public static Mod build(Value value) {
        return new Mod(value.get("coordinate").asLong(0), value.get("mod").asString(null));
    }
}
