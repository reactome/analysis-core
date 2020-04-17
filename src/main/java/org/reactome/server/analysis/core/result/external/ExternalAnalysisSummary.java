package org.reactome.server.analysis.core.result.external;

import org.reactome.server.analysis.core.model.AnalysisType;
import org.reactome.server.analysis.core.result.model.AnalysisSummary;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ExternalAnalysisSummary {

    private String token;
    private Integer version;
    private Boolean projection;
    private Boolean interactors;
    private String type;
    private String sampleName;
    private String server;
    private Boolean includeDisease;

    public ExternalAnalysisSummary() {
    }

    ExternalAnalysisSummary(AnalysisSummary summary, Integer version) {
        this.token = summary.getToken();
        this.version = version;
        this.projection = summary.isProjection();
        this.interactors = summary.isInteractors();
        this.type = summary.getType();
        this.sampleName = summary.getSampleName();
        this.server = summary.getServer();
        this.includeDisease = summary.isIncludeDisease();
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getVersion() {
        return version;
    }

    public Boolean getProjection() {
        return projection;
    }

    public Boolean getInteractors() {
        return interactors;
    }

    public AnalysisType getType() {
        return AnalysisType.getType(type);
    }

    public String getSampleName() {
        return sampleName;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Boolean getIncludeDisease() {
        return includeDisease;
    }

    public void setIncludeDisease(Boolean includeDisease) {
        this.includeDisease = includeDisease;
    }
}
