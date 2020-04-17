package org.reactome.server.analysis.core.result.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.reactome.server.analysis.core.model.AnalysisType;
import org.reactome.server.analysis.core.result.external.ExternalAnalysisSummary;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class AnalysisSummary {

    private String token;
    private Boolean projection;
    private Boolean interactors;
    private String type;
    private String sampleName;
    private Long species;
    private boolean text = false;
    private boolean includeDisease = true;
    private String fileName;
    private String server;

    AnalysisSummary(String token, Boolean projection, Boolean interactors, String sampleName, AnalysisType type, String server, Boolean includeDisease) {
        this.token = token;
        this.type = type.toString();
        this.sampleName = sampleName;
        this.projection = projection;
        this.interactors = interactors;
        this.server = server;
        this.includeDisease = includeDisease;
    }

    public AnalysisSummary(String token, Boolean projection, Boolean interactors, String sampleName, AnalysisType type, String fileName, String server, Boolean includeDisease) {
        this(token, projection, interactors, sampleName, type, server, includeDisease);
        this.fileName = fileName;
        if(this.fileName!=null) this.fileName = this.fileName.split("\\?")[0];
    }

    public AnalysisSummary(String token, Boolean projection, Boolean interactors, String sampleName, AnalysisType type, Long species, String server, Boolean includeDisease) {
        this(token, projection, interactors, sampleName, type, server, includeDisease);
        this.species = species;
    }

    public AnalysisSummary(String token, Boolean projection, Boolean interactors, String sampleName, AnalysisType type, boolean text, String server, Boolean includeDisease) {
        this(token, projection, interactors, sampleName, type, server, includeDisease);
        this.text = text;
    }

    public AnalysisSummary(String token, ExternalAnalysisSummary summary) {
        this(token, summary.getProjection(), summary.getInteractors(), summary.getSampleName(), summary.getType(), summary.getServer(), summary.getIncludeDisease());
    }

    public String getToken() {
        return token;
    }

    public Boolean isProjection() {
        return projection == null ? false : projection;
    }

    public Boolean isInteractors() {
        return interactors == null ? false : interactors;
    }

    public String getType() {
        return type;
    }

    public String getSampleName() {
        return sampleName;
    }

    public Long getSpecies() {
        return species;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isText() {
        return text;
    }

    @JsonIgnore
    public String getServer() {
        return server;
    }

    public boolean isIncludeDisease() {
        return includeDisease;
    }

    public void setIncludeDisease(boolean includeDisease) {
        this.includeDisease = includeDisease;
    }
}
