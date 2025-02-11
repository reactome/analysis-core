package org.reactome.server.analysis.core.result.model;

import org.reactome.server.analysis.core.model.PathwayNodeData;
import org.reactome.server.analysis.core.model.resource.MainResource;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
//@ApiModel(value = "ReactionStatistics", description = "Statistics for a reaction type")
public class ReactionStatistics extends Statistics {

    public ReactionStatistics(PathwayNodeData d, boolean importableOnly) {
        super("TOTAL", d.getReactionsCount(importableOnly), d.getReactionsFound(importableOnly), d.getReactionsRatio(importableOnly));
    }

    public ReactionStatistics(MainResource mainResource, PathwayNodeData d) {
        super(mainResource.getName(), d.getReactionsCount(mainResource), d.getReactionsFound(mainResource), d.getReactionsRatio(mainResource));
    }

}
