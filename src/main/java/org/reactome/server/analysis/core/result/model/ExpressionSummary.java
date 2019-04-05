package org.reactome.server.analysis.core.result.model;

import org.reactome.server.analysis.core.model.UserData;
import org.reactome.server.analysis.core.result.external.ExternalExpressionSummary;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ExpressionSummary {

    private List<String> columnNames;
    private Double min;
    private Double max;

    public ExpressionSummary(List<String> columnNames, Double min, Double max) {
        this.columnNames = columnNames;
        this.min = min;
        this.max = max;
    }

    public ExpressionSummary(UserData storedResult) {
        this.columnNames = storedResult.getExpressionColumnNames();
        this.min = storedResult.getExpressionBoundaries().getMin();
        this.max = storedResult.getExpressionBoundaries().getMax();
    }

    public ExpressionSummary(ExternalExpressionSummary summary){
        this.columnNames = summary.getColumnNames();
        this.min = summary.getMin();
        this.max = summary.getMax();
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public Double getMin() {
        return min;
    }

    public Double getMax() {
        return max;
    }
}
