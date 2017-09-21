package org.reactome.server.analysis.core.importer.query;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class OrthologyResult {

    private String originDatabaseName;
    private String originIdentifier;
    private String inferredToDatabaseName;
    private String inferredToIdentifier;

    public OrthologyResult() { }

    public String getOriginDatabaseName() {
        return originDatabaseName;
    }

    public void setOriginDatabaseName(String originDatabaseName) {
        this.originDatabaseName = originDatabaseName;
    }

    public String getOriginIdentifier() {
        return originIdentifier;
    }

    public void setOriginIdentifier(String originIdentifier) {
        this.originIdentifier = originIdentifier;
    }

    public String getInferredToDatabaseName() {
        return inferredToDatabaseName;
    }

    public void setInferredToDatabaseName(String inferredToDatabaseName) {
        this.inferredToDatabaseName = inferredToDatabaseName;
    }

    public String getInferredToIdentifier() {
        return inferredToIdentifier;
    }

    public void setInferredToIdentifier(String inferredToIdentifier) {
        this.inferredToIdentifier = inferredToIdentifier;
    }
}
