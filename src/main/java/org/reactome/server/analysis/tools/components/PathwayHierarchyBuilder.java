package org.reactome.server.analysis.tools.components;

import org.reactome.server.analysis.core.model.PathwayHierarchy;
import org.reactome.server.analysis.core.model.PathwayNode;
import org.reactome.server.analysis.core.model.SpeciesNode;
import org.reactome.server.analysis.core.model.SpeciesNodeFactory;
import org.reactome.server.analysis.core.util.MapSet;
import org.reactome.server.analysis.core.util.Pair;
import org.reactome.server.analysis.tools.BuilderTool;
import org.reactome.server.graph.domain.model.Event;
import org.reactome.server.graph.domain.model.Pathway;
import org.reactome.server.graph.domain.model.Species;
import org.reactome.server.graph.domain.model.TopLevelPathway;
import org.reactome.server.graph.service.SpeciesService;
import org.reactome.server.graph.service.TopLevelPathwayService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Luis Francisco Hernández Sánchez <luis.sanchez@uib.no>
 */
@Component
public class PathwayHierarchyBuilder {
    private static Logger logger = LoggerFactory.getLogger(PathwayHierarchyBuilder.class.getName());

    //Keeps the set of species identifiers used as main species in reactome
    private Set<Long> mainSpecies;
    private MapSet<Long, PathwayNode> pathwayLocation = new MapSet<Long, PathwayNode>();

    private Map<SpeciesNode, PathwayHierarchy> hierarchies = new HashMap<SpeciesNode, PathwayHierarchy>();  //This is the final structure that has to be filled and serialized

    public void build() {

        SpeciesService speciesService = ReactomeGraphCore.getService(SpeciesService.class);                             //This service will provide the list of species
        List<Species> speciesList = speciesService.getAllSpecies();

        TopLevelPathwayService topLevelPathwayService = ReactomeGraphCore.getService(TopLevelPathwayService.class);     // This service will provide the list top level pathwways for each species

        int i = 0;
        int tot = speciesList.size();

        for (Species species : speciesList) {

            if (BuilderTool.VERBOSE) {
                System.out.print("\rCreating the pathway hierarchies >> " + ++i + "/" + tot + " ");
            }

            SpeciesNode speciesNode = SpeciesNodeFactory.getSpeciesNode(species.getDbId(), species.getTaxId(), species.getDisplayName());
            PathwayHierarchy pathwayHierarchy = new PathwayHierarchy(speciesNode);                                      // Creates the empty hierarchy for each species
            this.hierarchies.put(speciesNode, pathwayHierarchy);                                                        // Add to the set of hierarchies

            //Get all top level pathways for the current species
            if (BuilderTool.VERBOSE) {
                logger.info("Getting pathways for " + species.getDisplayName() + " : " + species.getTaxId() + "\n");
            }
            Collection<TopLevelPathway> topLevelPathways = topLevelPathwayService.getTopLevelPathways(species.getDisplayName());
            if (BuilderTool.VERBOSE) {
                logger.info("Found " + topLevelPathways.size() + " top level pathways.\n");
            }

            Queue<Pair<Pathway, PathwayNode>> queue = new LinkedList<>();       //Queue to visit all the subpathways

            // Add to the hierarchy all the top level pathways
            // Adds to the queue all the possible starting points to visit subpathways (all top level pathways)
            for (TopLevelPathway topLevelPathway : topLevelPathways) {
                PathwayNode pathwayNode = pathwayHierarchy.addTopLevelPathway(topLevelPathway.getStId(), topLevelPathway.getDbId(), topLevelPathway.getDisplayName(), topLevelPathway.getHasDiagram());
                queue.add(new Pair<>(topLevelPathway, pathwayNode));
            }

            // Visit each sub pathway and add its sub pathways
            while (!queue.isEmpty()) {
                Pair<Pathway, PathwayNode> pair = queue.poll();                 // Visit first pathway in the list
                Pathway pathway = pair.getFst();
                PathwayNode pathwayNode = pair.getSnd();

                for (Event event : pathway.getHasEvent()) {                     // Get sub pathways for each top level pathway
                    if (event instanceof Pathway) {
                        Pathway subPathway = (Pathway) event;
                        PathwayNode subPathwayNode = pathwayNode.addChild(subPathway.getStId(), subPathway.getDbId(), subPathway.getDisplayName(), subPathway.getHasDiagram());      // Add pathway to hierarchy
                        queue.add(new Pair<>(subPathway, subPathwayNode));      // Add pathway to queue to check further subpathways later
                    }
                }
            }
            if (BuilderTool.VERBOSE) {
                logger.info("Finished " + species.getDisplayName() + "\n");
            }
        }
        if (BuilderTool.VERBOSE) {
            System.out.println("\rPathway hierarchies created successfully");
        }
    }

    public Map<SpeciesNode, PathwayHierarchy> getHierarchies() {
        return hierarchies;
    }

    public MapSet<Long, PathwayNode> getPathwayLocation() {
        return pathwayLocation;
    }

    protected void prepareToSerialise() {
        if (BuilderTool.VERBOSE) {
            System.out.print("Setting up the resource counters for each Resource/PathwayNode");
        }
        for (SpeciesNode species : this.hierarchies.keySet()) {
            PathwayHierarchy hierarchy = this.hierarchies.get(species);
            hierarchy.setCountersAndCleanUp();
        }
        if (BuilderTool.VERBOSE) {
            System.out.println("\rResource counters set up successfully.");
        }
    }
}
