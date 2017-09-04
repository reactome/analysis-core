package org.reactome.server.analysis.tools.components;

import org.reactome.server.analysis.core.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Luis Francisco Hernández Sánchez <luis.sanchez@uib.no>
 */

@Component
public class AnalysisBuilder {

    @Autowired
    private PathwayHierarchyBuilder pathwayHierarchyBuilder;

    public void build(String fileName){
        this.pathwayHierarchyBuilder = new PathwayHierarchyBuilder();
        this.pathwayHierarchyBuilder.build();       // Build memory data structure for the pathway hierarchy for each species

        DataContainer container = new DataContainer(pathwayHierarchyBuilder.getHierarchies());
    }
}
