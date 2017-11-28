package org.reactome.server.analysis.tools.components;

import org.slf4j.Logger;
import org.reactome.server.analysis.core.model.*;
import org.reactome.server.analysis.core.model.resource.MainResource;
import org.reactome.server.analysis.core.model.resource.Resource;
import org.reactome.server.analysis.core.model.resource.ResourceFactory;
import org.reactome.server.analysis.core.util.MapSet;
import org.reactome.server.analysis.core.util.Pair;
import org.reactome.server.analysis.tools.BuilderTool;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * For a previously loaded set of PhysicalEntities (mapped to the relevant pathways where they are present)
 * this class will decompose all of these and create a graph of the Reactome Physical Entities and a RADIX
 * TREE to map identifiers to Physical Entities.
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@Component
public class PhysicalEntityHierarchyBuilder {
    private static Logger logger = LoggerFactory.getLogger(PhysicalEntityHierarchyBuilder.class.getName());

    //The buffer is used in building time to avoid querying/decomposition of those previously processed
    private Map<Long, PhysicalEntityNode> physicalEntityBuffer = new HashMap<Long, PhysicalEntityNode>();

    //The RADIX-TREE with the map (identifiers -> [PhysicalEntityNode])
    private IdentifiersMap<PhysicalEntityNode> entitiesMap;

    //The graph representation of the PhysicalEntities in Reactome
    private PhysicalEntityGraph physicalEntityGraph;

    public PhysicalEntityHierarchyBuilder() {
        this.entitiesMap = new IdentifiersMap<>();
        this.physicalEntityGraph = new PhysicalEntityGraph();
    }

    public IdentifiersMap<PhysicalEntityNode> getEntitiesMap() {
        return this.entitiesMap;
    }

    public PhysicalEntityGraph getPhysicalEntityGraph() {
        return physicalEntityGraph;
    }

}
