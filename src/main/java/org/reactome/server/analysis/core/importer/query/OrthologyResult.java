package org.reactome.server.analysis.core.importer.query;

import org.neo4j.driver.Record;
import org.reactome.server.graph.domain.result.CustomQuery;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class OrthologyResult implements CustomQuery {

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

    @Override
    public CustomQuery build(Record r) {
        OrthologyResult or = new OrthologyResult();
        or.setInferredToDatabaseName(r.get("inferredToDatabaseName").asString(null));
        or.setInferredToIdentifier(r.get("inferredToIdentifier").asString(null));
        or.setOriginDatabaseName(r.get("originDatabaseName").asString(null));
        or.setOriginIdentifier(r.get("originIdentifier").asString(null));
        return or;
    }
}
