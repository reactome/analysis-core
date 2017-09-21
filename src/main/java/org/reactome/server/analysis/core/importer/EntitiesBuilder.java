package org.reactome.server.analysis.core.importer;

import org.reactome.server.analysis.core.Main;
import org.reactome.server.analysis.core.importer.query.*;
import org.reactome.server.analysis.core.model.*;
import org.reactome.server.analysis.core.model.identifier.MainIdentifier;
import org.reactome.server.analysis.core.model.resource.MainResource;
import org.reactome.server.analysis.core.model.resource.Resource;
import org.reactome.server.analysis.core.model.resource.ResourceFactory;
import org.reactome.server.graph.exception.CustomQueryException;
import org.reactome.server.graph.service.AdvancedDatabaseObjectService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class EntitiesBuilder {

    private static Logger logger = LoggerFactory.getLogger("importLogger");

    //Will contain the RADIX-TREE with the map (identifiers -> [EntityNode])
    private IdentifiersMap<EntityNode> entitiesMap;

    //A graph representation of the PhysicalEntities in Reactome
    private EntitiesContainer entitiesContainer;

    private AdvancedDatabaseObjectService ados = ReactomeGraphCore.getService(AdvancedDatabaseObjectService.class);

    public void build(Map<String, SpeciesNode> speciesMap) {
        Collection<SpeciesNode> speciesNodes = speciesMap.values();

        this.entitiesMap = new IdentifiersMap<>();
        this.entitiesContainer = new EntitiesContainer();

        Map<String, PsiModNode> psiModNodeMap = PsiModImporter.getPsiModMap();

        String msgPrefix = "\rCreating the entities container ";

        String query;
        Map<String, Object> paramsMap = new HashMap<>();
        int s = 0; int st = speciesNodes.size();
        for (SpeciesNode species : speciesNodes) {
            if (Main.TEST_MAIN_SPECIES && !species.getTaxID().equals(Main.MAIN_SPECIES_TAX_ID)) continue;

            paramsMap.put("taxId", species.getTaxID());

            String speciesPrefix = "for '" + species.getName() + "' (" + (++s) + "/" + st + ")";
            if (Main.VERBOSE) System.out.print(msgPrefix + speciesPrefix + " >> retrieving xrefs...");

            query = "MATCH (:Species{taxId:{taxId}})<-[:species]-(:Pathway)-[:hasEvent]->(rle:ReactionLikeEvent), " +
                    "      (rle)-[:input|output|catalystActivity|entityFunctionalStatus|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate|repeatedUnit|referenceEntity*]->(re:ReferenceEntity) " +
                    "WITH DISTINCT re " +
                    "OPTIONAL MATCH (re)-[:crossReference]->(dbi:DatabaseIdentifier) " +
                    "WITH DISTINCT re, COLLECT(DISTINCT {databaseName: dbi.databaseName, identifier: dbi.identifier}) AS dbis, COUNT(DISTINCT dbi) AS n " +
                    "RETURN re.dbId AS referenceEntity, " +
                    "       re.secondaryIdentifier AS  secondaryIdentifiers, " +
                    "       re.geneName AS geneNames, " +
                    "       re.otherIdentifier AS otherIdentifiers, " +
                    "       [{databaseName: re.databaseName, " +
                    "         identifier: CASE WHEN re.variantIdentifier IS NOT NULL THEN re.variantIdentifier ELSE re.identifier END " +
                    "         }] + CASE WHEN n = 0 THEN [] ELSE dbis END AS xrefs";

            Map<Long, ReferenceEntityIdentifiers> xrefMap = new HashMap<>();
            try {
                for (ReferenceEntityIdentifiers aux : ados.customQueryForObjects(ReferenceEntityIdentifiers.class, query, paramsMap)) {
                    xrefMap.put(aux.getReferenceEntity(), aux);
                }
            } catch (CustomQueryException e) {
                throw new RuntimeException(e);
            }

            if (Main.VERBOSE) System.out.print(msgPrefix + speciesPrefix + " >> retrieving participants...");

            query = "MATCH (:Species{taxId:{taxId}})<-[:species]-(p:Pathway)-[:hasEvent]->(rle:ReactionLikeEvent), " +
                    "      (rle)-[:input|output|catalystActivity|entityFunctionalStatus|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate|repeatedUnit*]->(pe:PhysicalEntity)-[:referenceEntity]->(re:ReferenceEntity) " +
                    "WITH DISTINCT p, pe, re, COLLECT(DISTINCT {dbId: rle.dbId, stId: rle.stId}) AS rles " +
                    "OPTIONAL MATCH (pe)-[:hasModifiedResidue]->(tm:TranslationalModification)-[:psiMod]->(mod:PsiMod) " +
                    "WITH DISTINCT p, pe, re, rles, COLLECT(DISTINCT {coordinate: tm.coordinate, mod: mod.identifier}) AS tmods, COUNT(DISTINCT mod) AS n " +
                    "RETURN p.dbId AS pathway, " +
                    "       pe.dbId AS physicalEntity, " +
                    "       pe.speciesName AS speciesName, " +
                    "       re.dbId as referenceEntity, " +
                    "       rles AS reactions, " +
                    "       CASE WHEN n = 0 THEN [] ELSE tmods END AS mods";

            Collection<EntitiesQueryResult> result;
            try {
                result = ados.customQueryForObjects(EntitiesQueryResult.class, query, paramsMap);
            } catch (CustomQueryException e) {
                throw new RuntimeException(e);
            }

            int i = 0; int tot = result.size();
            for (EntitiesQueryResult current : result) {
                if (Main.VERBOSE && ++i % 25 == 0) System.out.print(msgPrefix + speciesPrefix + " >> " + i + "/" + tot);

                ReferenceEntityIdentifiers rei = xrefMap.get(current.getReferenceEntity());
                List<XRef> xrefs = rei.getXrefs();
                final String databaseName = xrefs.get(0).getDatabaseName();
                final String identifier = xrefs.get(0).getIdentifier();
                final SpeciesNode entitySpecies = speciesMap.get(current.getSpeciesName()); //Small molecules have no species (null is assigned)
                Resource resource = ResourceFactory.getResource(databaseName);
                if (resource instanceof MainResource) {
                    List<Modification> modifications = new ArrayList<>();
                    for (Mod mod : current.getMods()) {
                        modifications.add(new Modification(mod.getCoordinate(), psiModNodeMap.get(mod.getMod())));
                    }
                    MainResource mainResource = (MainResource) resource;
                    //IMPORTANT: Add checks for duplicates and returns the right entity node to "play with"
                    EntityNode node = entitiesContainer.add(new EntityNode(entitySpecies, mainResource, identifier, modifications));
                    node.addPathwayReactions(current.getPathwayReactions());

                    //Adding every possible xRef to the RadixTree
                    for (XRef xref : xrefs) {
                        resource = ResourceFactory.getResource(xref.getDatabaseName());
                        entitiesMap.add(xref.getIdentifier(), resource, node);
                    }

                    resource = ResourceFactory.getResource("#" + databaseName);

                    for (String secondaryIdentifier : rei.getSecondaryIdentifiers()) {
                        entitiesMap.add(secondaryIdentifier, resource, node);
                    }

                    for (String geneName : rei.getGeneNames()) {
                        entitiesMap.add(geneName, resource, node);
                    }

                    for (String otherIdentifier : rei.getOtherIdentifiers()) {
                        entitiesMap.add(otherIdentifier, resource, node);
                    }

                } else {
                    logger.error("There is not main resource for physical entity " + current.getPhysicalEntity());
                }
            }
        }

        if (Main.VERBOSE) System.out.println(msgPrefix + ">> Done.");
    }

    public void setOrthologous() {
        if (Main.TEST_MAIN_SPECIES) return;

        String msgPrefix = "\rSetting orthologies between species entities >> ";
        if (Main.VERBOSE) System.out.print(msgPrefix + " retrieving relationships...");

        String query = "" +
                "MATCH (re1:ReferenceEntity)<-[:referenceEntity]-(:PhysicalEntity)-[:inferredTo]->(:PhysicalEntity)-[:referenceEntity]->(re2:ReferenceEntity) " +
                "RETURN DISTINCT re1.databaseName AS originDatabaseName, re1.identifier AS originIdentifier, re2.databaseName AS inferredToDatabaseName, re2.identifier AS inferredToIdentifier";
        Map<String, Object> paramsMap = new HashMap<>();

        Collection<OrthologyResult> orthologyResults;
        try {
            orthologyResults = ados.customQueryForObjects(OrthologyResult.class, query, paramsMap);
        } catch (CustomQueryException e) {
            e.printStackTrace();
            return;
        }

        int i = 0; int t = orthologyResults.size();
        for (OrthologyResult orthologyResult : orthologyResults) {
            if (Main.VERBOSE) System.out.print(msgPrefix + (++i) + " / " + t);

            MainResource originResource = (MainResource) ResourceFactory.getResource(orthologyResult.getOriginDatabaseName());
            MainResource inferredToResource = (MainResource) ResourceFactory.getResource(orthologyResult.getInferredToDatabaseName());

            MainIdentifier origin = new MainIdentifier(originResource, new AnalysisIdentifier(orthologyResult.getOriginIdentifier()));
            MainIdentifier inferredTo = new MainIdentifier(inferredToResource, new AnalysisIdentifier(orthologyResult.getInferredToIdentifier()));

            Set<EntityNode> nodesFrom = entitiesContainer.getNodes(origin);
            Set<EntityNode> nodesTo = entitiesContainer.getNodes(inferredTo);
            if (nodesFrom != null && !nodesFrom.isEmpty() && nodesTo != null && !nodesTo.isEmpty()){
                for (EntityNode from : nodesFrom) {
                    for (EntityNode to : nodesTo) {
                        from.addInferredTo(to);
                        to.addInferredFrom(from);
                    }
                }
            }
        }

        if(Main.VERBOSE) System.out.println(msgPrefix + "Done.");
    }

    public EntitiesContainer getEntitiesContainer() {
        return entitiesContainer;
    }

    public IdentifiersMap<EntityNode> getEntitiesMap() {
        return entitiesMap;
    }
}