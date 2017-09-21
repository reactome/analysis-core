package org.reactome.server.analysis.core.importer.query;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class XRef {

    private String databaseName;
    private String identifier;

    public XRef(String databaseName, String identifier) {
        this.databaseName = databaseName;
        this.identifier = identifier;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getIdentifier() {
        return identifier;
    }
}
