package org.reactome.server.analysis.core.importer.query;

import org.neo4j.driver.Value;
import org.springframework.lang.NonNull;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class XRef {

    private String databaseName;
    private String identifier;

    public XRef() { }

    public XRef(String databaseName, String identifier) {
        this.databaseName = databaseName;
        this.identifier = identifier;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(@NonNull String databaseName) {
        this.databaseName = databaseName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(@NonNull String identifier) {
        this.identifier = identifier;
    }

    public static XRef build(Value value) {
        return new XRef(value.get("databaseName").asString(), value.get("identifier").asString());
    }
}
