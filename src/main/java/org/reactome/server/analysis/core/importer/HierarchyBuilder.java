package org.reactome.server.analysis.core.importer;

import org.reactome.server.analysis.core.Main;
import org.reactome.server.analysis.core.model.PathwayHierarchy;
import org.reactome.server.analysis.core.model.PathwayNode;
import org.reactome.server.analysis.core.model.SpeciesNode;
import org.reactome.server.analysis.core.model.SpeciesNodeFactory;
import org.reactome.server.analysis.core.util.MapSet;
import org.reactome.server.graph.domain.model.Event;
import org.reactome.server.graph.domain.model.Pathway;
import org.reactome.server.graph.domain.model.Species;
import org.reactome.server.graph.domain.model.TopLevelPathway;
import org.reactome.server.graph.service.SpeciesService;
import org.reactome.server.graph.service.TopLevelPathwayService;
import org.reactome.server.graph.utils.ReactomeGraphCore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class HierarchyBuilder {

    private Map<SpeciesNode, PathwayHierarchy> hierarchies = new HashMap<>();
    private MapSet<Long, PathwayNode> pathwayLocation = new MapSet<>();

    public void build() {
        String msgPrefix = "\rCreating the pathway hierarchies";

        SpeciesService speciesService = ReactomeGraphCore.getService(SpeciesService.class);
        TopLevelPathwayService tlpService = ReactomeGraphCore.getService(TopLevelPathwayService.class);

        List<Species> speciesList = speciesService.getSpecies();
        int i = 0;
        int tot = Main.TEST_MAIN_SPECIES ? 1 : speciesList.size();
        for (Species species : speciesList) {
            //FOR TEST PURPOSES
            if(Main.TEST_MAIN_SPECIES && !species.getTaxId().equals(Main.MAIN_SPECIES_TAX_ID)) break;

            if (Main.VERBOSE) System.out.print(msgPrefix + " '" + species.getDisplayName() + "' >> " + ++i + "/" + tot + " ");

            SpeciesNode speciesNode = SpeciesNodeFactory.getSpeciesNode(species.getDbId(), species.getTaxId(), species.getDisplayName());
            PathwayHierarchy pathwayHierarchy = new PathwayHierarchy(speciesNode);
            this.hierarchies.put(speciesNode, pathwayHierarchy);

            for (TopLevelPathway tlp : tlpService.getTopLevelPathways(species.getTaxId())) {
                PathwayNode node = pathwayHierarchy.addTopLevelPathway(tlp);
                this.pathwayLocation.add(tlp.getDbId(), node);
                this.fillBranch(node, tlp);
                if (Main.VERBOSE) {
                    System.out.print("."); // Indicates progress
                }
            }
        }

        if(Main.VERBOSE) System.out.println(msgPrefix + " >> Done.");
    }

    public Map<SpeciesNode, PathwayHierarchy> getHierarchies() {
        return hierarchies;
    }

    public MapSet<Long, PathwayNode> getPathwayLocation() {
        return pathwayLocation;
    }

    public Map<String, SpeciesNode> getSpeciesMap(){
        Map<String, SpeciesNode> rtn = new HashMap<>();
        for (SpeciesNode speciesNode : hierarchies.keySet()) {
            rtn.put(speciesNode.getName(), speciesNode);
        }
        return rtn;
    }

    private void fillBranch(PathwayNode node, Pathway pathway) {
        for (Event event : pathway.getHasEvent()) {
            if (event instanceof Pathway) {
                Pathway p = (Pathway) event;
                PathwayNode aux = node.addChild(p);
                this.pathwayLocation.add(p.getDbId(), aux);
                this.fillBranch(aux, p);
            }
        }
    }

    public void prepareToSerialise(){
        if(Main.VERBOSE) {
            System.out.print("Setting up the resource counters for each Resource/PathwayNode");
        }
        for (SpeciesNode species : this.hierarchies.keySet()) {
            PathwayHierarchy hierarchy = this.hierarchies.get(species);
            hierarchy.setCountersAndCleanUp();
        }
        if(Main.VERBOSE) {
            System.out.println("\rResource counters set up successfully.");
        }
    }
}
