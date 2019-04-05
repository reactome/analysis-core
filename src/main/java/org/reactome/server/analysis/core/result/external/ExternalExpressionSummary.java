package org.reactome.server.analysis.core.result.external;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.reactome.server.analysis.core.result.model.ExpressionSummary;

import java.util.ArrayList;
import java.util.List;

public class ExternalExpressionSummary {

    private List<String> columnNames;
    private Double min;
    private Double max;

    public ExternalExpressionSummary() {
    }

    ExternalExpressionSummary(ExpressionSummary summary) {
        this.columnNames = new ArrayList<>(summary.getColumnNames());
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

    @JsonIgnore
    public int getExpValues(){
        if (columnNames == null) return 0;
        return columnNames.size();
    }

    public boolean isValid(Double exp){
        if (min == null || max == null || exp == null) return false;
        return min <= exp && exp <= max;
    }
}
