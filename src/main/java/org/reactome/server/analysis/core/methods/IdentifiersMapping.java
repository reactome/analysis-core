package org.reactome.server.analysis.core.methods;


import org.reactome.server.analysis.core.data.AnalysisData;
import org.reactome.server.analysis.core.model.EntityNode;
import org.reactome.server.analysis.core.model.IdentifiersMap;
import org.reactome.server.analysis.core.model.InteractorNode;
import org.reactome.server.analysis.core.model.SpeciesNode;
import org.reactome.server.analysis.core.model.identifier.MainIdentifier;
import org.reactome.server.analysis.core.model.resource.Resource;
import org.reactome.server.analysis.core.result.model.MappedEntity;
import org.reactome.server.analysis.core.result.model.MappedIdentifier;
import org.reactome.server.analysis.core.util.MapSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@Component
public class IdentifiersMapping {

    private static Logger logger = LoggerFactory.getLogger("methodsLogger");

    private static final Object MAPPING_SEMAPHORE = new Object();
    private static long MAPPING_COUNT = 0L;

    private final AnalysisData analysisData;

    @Autowired
    public IdentifiersMapping(AnalysisData analysisData) {
        this.analysisData = analysisData;
    }

    public List<MappedEntity> run(Set<String> identifiers, SpeciesNode speciesNode, boolean includeInteractors) {
        this.increaseCounter();
        MapSet<String, MappedIdentifier> mapping = getMapping(identifiers, speciesNode, includeInteractors);
        this.decreaseCounter();
        List<MappedEntity> rtn = new ArrayList<>();
        for (String identifier : mapping.keySet()) {
            rtn.add(new MappedEntity(identifier, mapping.getElements(identifier)));
        }
        return rtn;
    }

    public static long getMappingCount() {
        return MAPPING_COUNT;
    }

    private MapSet<String, MappedIdentifier> getMapping(Set<String> identifiers, SpeciesNode speciesNode, boolean includeInteractors) {
        MapSet<String, MappedIdentifier> rtn = new MapSet<>();

        final int originalSampleSize = identifiers.size();
        logger.trace("Mapping: " + originalSampleSize + " identifier(s). Including interactors: " + includeInteractors + ". Project to species: " + (speciesNode == null ? false : speciesNode.getName()));
        long start = System.currentTimeMillis();

        IdentifiersMap<EntityNode> entitiesMap = analysisData.getEntitiesMap();
        for (String identifier : identifiers) {
            rtn.add(identifier, new HashSet<>());

            MapSet<Resource, EntityNode> resourceEntities = entitiesMap.get(identifier);
            for (Resource resource : resourceEntities.keySet()) {
                for (EntityNode node : resourceEntities.getElements(resource)) {
                    if (speciesNode != null) node = node.getProjection(speciesNode);
                    if (node != null) rtn.add(identifier, new MappedIdentifier(node.getIdentifier()));
                }
            }

            if (includeInteractors) {
                IdentifiersMap<InteractorNode> interactorsMap = analysisData.getInteractorsMap();
                MapSet<Resource, InteractorNode> interactors = interactorsMap.get(identifier);
                for (Resource resource : interactors.keySet()) {
                    for (InteractorNode interactorNode : interactors.getElements(resource)) {
                        Set<MainIdentifier> interactsWith = interactorNode.getInteractsWith(); //InteractorHelper.getInteractsWith(speciesNode, entitiesMap, interactorNode);
                        rtn.add(identifier, new MappedIdentifier(resource, interactorNode.getAccession(), interactsWith));
                    }
                }
            }
        }

        long end = System.currentTimeMillis();
        logger.info("Mapping for " + originalSampleSize + " identifier(s) performed in " + (end - start) + " ms");

        return rtn;
    }



    private void decreaseCounter() {
        synchronized (MAPPING_SEMAPHORE) {
            if (--MAPPING_COUNT == 0) {
                MAPPING_SEMAPHORE.notify(); //Only one background producer should be using this semaphore
            }
        }
    }

    private void increaseCounter() {
        synchronized (MAPPING_SEMAPHORE) {
            ++MAPPING_COUNT;
        }
    }
}
