package org.reactome.server.analysis.core.importer.query;

import javax.annotation.Nonnull;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
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

    public void setDatabaseName(@Nonnull String databaseName) {
        this.databaseName = databaseName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(@Nonnull String identifier) {
        this.identifier = identifier;
    }
}
