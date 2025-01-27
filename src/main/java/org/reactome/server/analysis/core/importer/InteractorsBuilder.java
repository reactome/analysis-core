package org.reactome.server.analysis.core.importer;

import org.reactome.server.analysis.core.Main;
import org.reactome.server.analysis.core.importer.query.InteractorsTargetQueryResult;
import org.reactome.server.analysis.core.model.*;
import org.reactome.server.analysis.core.model.identifier.MainIdentifier;
import org.reactome.server.analysis.core.model.resource.MainResource;
import org.reactome.server.analysis.core.model.resource.Resource;
import org.reactome.server.analysis.core.model.resource.ResourceFactory;
import org.reactome.server.analysis.core.util.MapSet;
import org.reactome.server.graph.domain.model.*;
import org.reactome.server.graph.exception.CustomQueryException;
import org.reactome.server.graph.service.AdvancedDatabaseObjectService;
import org.reactome.server.graph.service.InteractionsService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsBuilder {

    private static Logger logger = LoggerFactory.getLogger("importLogger");

    private AdvancedDatabaseObjectService ados = ReactomeGraphCore.getService(AdvancedDatabaseObjectService.class);

    //Will contain the RADIX-TREE with the map (identifiers -> [InteractorNode])
    private IdentifiersMap<InteractorNode> interactorsMap = new IdentifiersMap<>();

    //Keeps track of the number of interactors that have been included
    private int n = 0;

    public void build(Set<SpeciesNode> speciesNodes, EntitiesContainer entities, InteractionsService interactionsService) {
        if (Main.VERBOSE) System.out.print("Starting creation of the interactors container...");

        String query;
        Map<String, Object> paramsMap = new HashMap<>();
        int s = 0;
        int st = speciesNodes.size();
        for (SpeciesNode species : speciesNodes) {
            if (Main.TEST_MAIN_SPECIES && !species.getTaxID().equals(Main.MAIN_SPECIES_TAX_ID)) continue;

            paramsMap.put("taxId", species.getTaxID());

            String speciesPrefix = "'" + species.getName() + "' (" + (++s) + "/" + st + ")";
            if (Main.VERBOSE) System.out.print("\rCreating interactors container for " + speciesPrefix + " >> retrieving targets...");


            query = "" +
              "MATCH (:Species{taxId:$taxId})<-[:species]-(p:Pathway)-[:hasEvent]->(rle:ReactionLikeEvent), " +
              "      (rle)-[:input|output|catalystActivity|physicalEntity|entityFunctionalStatus|diseaseEntity|" +
              "regulatedBy|regulator*]->(pe:PhysicalEntity)-[:referenceEntity]->(re:ReferenceEntity) " +
              "WHERE (p:TopLevelPathway) OR (:TopLevelPathway)-[:hasEvent*]->(p) " +
              //"     AND NOT (pe)-[:hasModifiedResidue]->(:TranslationalModification) " +
              "WITH DISTINCT p, re, COLLECT(DISTINCT {dbId: rle.dbId, stId: rle.stId}) AS rles " +
              "RETURN DISTINCT re.databaseName AS databaseName, " +
              "                CASE WHEN re.variantIdentifier IS NOT NULL THEN re.variantIdentifier ELSE re.identifier END AS identifier, " +
              "                p.dbId AS pathway, " +
              "                rles AS reactions";

            Collection<InteractorsTargetQueryResult> its;
            try {
                its = ados.getCustomQueryResults(InteractorsTargetQueryResult.class, query, paramsMap);
            } catch (CustomQueryException e) {
                throw new RuntimeException(e);
            }

            if (Main.VERBOSE) System.out.print("\rInteractors retrieved for " + speciesPrefix + " >> aggregating results by entity identifier...");
            MapSet<MainIdentifier, MapSet<Long, AnalysisReaction>> compressedResult = new MapSet<>();
            for (InteractorsTargetQueryResult it : its) {
                Resource resource = ResourceFactory.getResource(it.getDatabaseName());
                if(resource instanceof MainResource) {
                    MainResource mr = (MainResource) resource;
                    AnalysisIdentifier ai = new AnalysisIdentifier(it.getIdentifier());

                    MainIdentifier interactsWith = new MainIdentifier(mr, ai);
                    if (entities.getNodes(interactsWith).isEmpty())
                        logger.error(interactsWith + " hasn't been previously created for '" + species.getName() + "'.");

                    compressedResult.add(interactsWith, it.getPathwayReactions());
                }
            }

            int i = 0, tot = compressedResult.keySet().size();
            for (MainIdentifier target : compressedResult.keySet()) {
                if (Main.VERBOSE) System.out.print("\rRetrieving interactors for targets in " + speciesPrefix + " >> " + (++i) + "/" + tot);
                String acc = target.getValue().getId();

                for (Interaction interaction : interactionsService.getInteractions(acc)) {
                    ReferenceEntity interactor;
                    ReferenceEntity interactsWith;
                    if (interaction instanceof UndirectedInteraction) {
                        UndirectedInteraction ui = (UndirectedInteraction) interaction;
                        interactor = ui.getInteractor().get(0);
                        interactsWith = ui.getInteractor().size() == 1 ? interactor : ui.getInteractor().get(1);
                    } else {
                        DirectedInteraction di = (DirectedInteraction) interaction;
                        interactor = di.getTarget();
                        interactsWith = di.getSource();
                    }

                    Resource resource = ResourceFactory.getResource(interactsWith.getDatabaseName());
                    String variantIdentifier = interactor.fetchSingleValue("getVariantIdentifier");
                    String identifier = variantIdentifier != null ? variantIdentifier : interactor.getIdentifier();
                    InteractorNode interactorNode = getOrCreate(resource, identifier);
                    for (MapSet<Long, AnalysisReaction> prs : compressedResult.getElements(target)) {
                        for (Long pathwayId : prs.keySet()) {
                            interactorNode.addInteractsWith(pathwayId, target);
                            interactorNode.addPathwayReactions(prs);
                        }
                    }

                    if (interactor instanceof ReferenceSequence) {
                        ReferenceSequence rs = (ReferenceSequence) interactor;
                        if (rs.getGeneName() != null) {
                            for (String alias : rs.getGeneName()) {
                                interactorsMap.add(alias, resource, interactorNode);
                            }
                        }
                        if (rs.getSecondaryIdentifier() != null){
                            for (String si : rs.getSecondaryIdentifier()) {
                                if(!si.contains(" ")) interactorsMap.add(si, resource, interactorNode);
                            }
                        }
                    }
                }
            }
        }
        if (Main.VERBOSE)
            System.out.println("\rInteractors container successfully created >> " + n + " interactors have been added to Reactome.");
    }

    public IdentifiersMap<InteractorNode> getInteractorsMap() {
        return interactorsMap;
    }

    private InteractorNode getOrCreate(Resource resource, String identifier) {
        MapSet<Resource, InteractorNode> map = interactorsMap.get(identifier);
        Set<InteractorNode> interactors = map.getElements(resource);
        if (interactors == null || interactors.isEmpty()) {
            InteractorNode interactorNode = new InteractorNode(identifier);
            interactorsMap.add(identifier, resource, interactorNode);
            n++;
            return interactorNode;
        } else {
            //Using IdentifiersMap causes this "oddity" here, but is a minor inconvenient
            if (interactors.size() > 1)
                   logger.error("Duplicate interactors found for " + identifier + " [" + resource.getName() + "]");
            return interactors.iterator().next();
        }
    }
}
