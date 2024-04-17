package org.reactome.server.analysis.core.result.external;

import org.reactome.server.analysis.core.model.AnalysisIdentifier;
import org.reactome.server.analysis.core.result.AnalysisStoredResult;
import org.reactome.server.analysis.core.result.PathwayNodeSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ExternalAnalysisResult {

    private ExternalAnalysisSummary summary;
    private ExternalExpressionSummary expressionSummary;
    private List<ExternalPathwayNodeSummary> pathways;
    private List<ExternalIdentifier> notFound;
    private List<String> warnings;

    public ExternalAnalysisResult() {
    }

    public ExternalAnalysisResult(AnalysisStoredResult result, Integer version) {
        this(result, version, false);
    }

    public ExternalAnalysisResult(AnalysisStoredResult result, Integer version, boolean importableOnly) {
        this.summary = new ExternalAnalysisSummary(result.getSummary(), version);
        this.expressionSummary = new ExternalExpressionSummary(result.getExpressionSummary());

        Set<ExternalIdentifier> filteredIdentifiers = result.getAnalysisIdentifiers().stream().map(ExternalIdentifier::new).collect(Collectors.toSet());
        this.pathways = new ArrayList<>();
        for (PathwayNodeSummary pns : result.getPathways()) {
            ExternalPathwayNodeSummary e = new ExternalPathwayNodeSummary(pns, importableOnly);
            if (!e.getData().getEntities().isEmpty()) {
                this.pathways.add(e);
                if (importableOnly) {
                    e.getData().getEntities().forEach(filteredIdentifiers::remove);
                }
            }

        }

        this.notFound = new ArrayList<>();
        for (AnalysisIdentifier ai : result.getNotFound()) {
            this.notFound.add(new ExternalIdentifier(ai));
        }
        if (importableOnly) {
            this.notFound.addAll(filteredIdentifiers);
        }

        if (result.getWarnings() != null) {
            this.warnings = new ArrayList<>(result.getWarnings());
        }

    }

    public ExternalAnalysisSummary getSummary() {
        return summary;
    }

    public ExternalExpressionSummary getExpressionSummary() {
        return expressionSummary;
    }

    public List<ExternalPathwayNodeSummary> getPathways() {
        return pathways;
    }

    public List<ExternalIdentifier> getNotFound() {
        return notFound;
    }

    public List<String> getWarnings() {
        return warnings;
    }
}
