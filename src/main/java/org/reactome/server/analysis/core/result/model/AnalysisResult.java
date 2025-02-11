package org.reactome.server.analysis.core.result.model;


import org.reactome.server.analysis.core.result.AnalysisStoredResult;

import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class AnalysisResult {

    private AnalysisSummary summary;
    private ExpressionSummary expression;
    private Integer identifiersNotFound;
    private Integer pathwaysFound;
    private List<PathwaySummary> pathways;
    private List<ResourceSummary> resourceSummary;
    private List<SpeciesSummary> speciesSummary;
    private List<String> warnings;

    public AnalysisResult(AnalysisStoredResult storedResult, List<PathwaySummary> pathways){
        this.summary = storedResult.getSummary();
        this.pathways = pathways;
        this.identifiersNotFound = storedResult.getNotFound().size();
        this.pathwaysFound = storedResult.getPathways().size();
        this.expression = storedResult.getExpressionSummary();
        this.resourceSummary = storedResult.getResourceSummary();
        this.warnings = storedResult.getWarnings();
        this.speciesSummary = storedResult.getSpeciesSummary();
    }

    public AnalysisSummary getSummary() {
        return summary;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public Integer getPathwaysFound() {
        return pathwaysFound;
    }

    public Integer getIdentifiersNotFound() {
        return identifiersNotFound;
    }

    public List<PathwaySummary> getPathways() {
        return pathways;
    }

    public List<ResourceSummary> getResourceSummary() {
        return resourceSummary;
    }

    public List<SpeciesSummary> getSpeciesSummary() {
        return speciesSummary;
    }

    public ExpressionSummary getExpression() {
        return expression;
    }
}
