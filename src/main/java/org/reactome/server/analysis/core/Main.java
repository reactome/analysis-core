package org.reactome.server.analysis.core;

import com.martiansoftware.jsap.*;
import org.reactome.server.analysis.core.config.ReactomeNeo4jConfig;
import org.reactome.server.analysis.core.data.AnalysisDataUtils;
import org.reactome.server.analysis.core.importer.EntitiesBuilder;
import org.reactome.server.analysis.core.importer.HierarchyBuilder;
import org.reactome.server.analysis.core.importer.InteractorsBuilder;
import org.reactome.server.analysis.core.model.*;
import org.reactome.server.analysis.core.model.identifier.InteractorIdentifier;
import org.reactome.server.analysis.core.model.identifier.MainIdentifier;
import org.reactome.server.analysis.core.util.FileUtil;
import org.reactome.server.analysis.core.util.MapSet;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import org.reactome.server.interactors.database.InteractorsDatabase;

import java.io.File;
import java.sql.SQLException;
import java.util.Set;

public class Main {

    public static final boolean VERBOSE = true;
    public static final String MAIN_SPECIES_TAX_ID = "9606";
    public static final boolean TEST_MAIN_SPECIES = true;

    public static void main(String[] args) throws JSAPException {

        // Program Arguments -h, -p, -u, -k
        SimpleJSAP jsap = new SimpleJSAP(Main.class.getName(), "Connect to Reactome Graph Database",
                new Parameter[]{
                        new FlaggedOption("host", JSAP.STRING_PARSER, "localhost", JSAP.NOT_REQUIRED, 'h', "host", "The neo4j host")
                        , new FlaggedOption("port", JSAP.STRING_PARSER, "7474", JSAP.NOT_REQUIRED, 'p', "port", "The neo4j port")
                        , new FlaggedOption("user", JSAP.STRING_PARSER, "neo4j", JSAP.NOT_REQUIRED, 'u', "user", "The neo4j user")
                        , new FlaggedOption("password", JSAP.STRING_PARSER, "neo4j", JSAP.REQUIRED, 'k', "password", "The neo4j password")
                        , new FlaggedOption("output", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, 'o', "output",
                        "The file where the results are written to")
                        , new FlaggedOption("interactors-database-path", JSAP.STRING_PARSER, null, JSAP.REQUIRED, 'g', "interactors-database-path",
                        "Interactor Database Path")
                        , new QualifiedSwitch("verbose", JSAP.BOOLEAN_PARSER, null, JSAP.NOT_REQUIRED, 'v', "verbose",
                        "Requests verbose output")
                }
        );

        JSAPResult config = jsap.parse(args);
        if (jsap.messagePrinted()) System.exit(1);

        //Initialising ReactomeCore Neo4j configuration
        ReactomeGraphCore.initialise(config.getString("host"),
                config.getString("port"),
                config.getString("user"),
                config.getString("password"),
                ReactomeNeo4jConfig.class);

        String database = config.getString("interactors-database-path");
        File databaseFile = new File(database);
        if (!databaseFile.exists()) {
            throw new RuntimeException("Interactor database does not exist");
        }

        String fileName = config.getString("output");
        FileUtil.checkFileName(fileName);

        InteractorsDatabase interactorsDatabase = null;
        try {
            interactorsDatabase = new InteractorsDatabase(database);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        HierarchyBuilder hierarchyBuilder = new HierarchyBuilder();
        hierarchyBuilder.build();

        EntitiesBuilder entitiesBuilder = new EntitiesBuilder();
        entitiesBuilder.build(hierarchyBuilder.getHierarchies().keySet());
        entitiesBuilder.setOrthologous();

        InteractorsBuilder interactorsBuilder = new InteractorsBuilder();
//        interactorsBuilder.build(entitiesBuilder.getEntitiesContainer(), interactorsDatabase);

        calculateNumbersInHierarchyNodesForMainResources(hierarchyBuilder, entitiesBuilder, interactorsBuilder);

        DataContainer container = new DataContainer(hierarchyBuilder.getHierarchies(),
                hierarchyBuilder.getPathwayLocation(),
                entitiesBuilder.getEntitiesContainer(),
                entitiesBuilder.getEntitiesMap(),
                interactorsBuilder.getInteractorsMap());
        AnalysisDataUtils.kryoSerialisation(container, fileName);

        /*
        if(VERBOSE) System.out.println("The intermediate data file has been generated.");

        HierarchiesDataProducer.initializeProducer(container);
        AnalysisData analysisData = new AnalysisData(container);
        EnrichmentAnalysis ora = new EnrichmentAnalysis(analysisData);

        Set<AnalysisIdentifier> identifiers = new HashSet<>();
        identifiers.add(new AnalysisIdentifier("PTEN"));
        HierarchiesData result = ora.overRepresentation(identifiers, SpeciesNodeFactory.getHumanNode(), false);
        System.out.println("DONE");
        HierarchiesDataProducer.interruptProducer();
        */
    }


    private static void calculateNumbersInHierarchyNodesForMainResources(HierarchyBuilder hierarchyBuilder,
                                                                         EntitiesBuilder entitiesBuilder,
                                                                         InteractorsBuilder interactorsBuilder) {

        MapSet<Long, PathwayNode> pathwayLocation = hierarchyBuilder.getPathwayLocation();
        EntitiesContainer entitiesContainer = entitiesBuilder.getEntitiesContainer();
        IdentifiersMap<InteractorNode> interactorsMap = interactorsBuilder.getInteractorsMap();

        for (EntityNode physicalEntityNode : entitiesContainer.getAllNodes()) {
            MainIdentifier mainIdentifier = physicalEntityNode.getIdentifier();
            if (mainIdentifier != null) {
                for (Long pathwayId : physicalEntityNode.getPathwayIds()) {
                    Set<PathwayNode> pNodes = pathwayLocation.getElements(pathwayId);
                    if (pNodes == null) continue;
                    for (PathwayNode pathwayNode : pNodes) {
                        Set<AnalysisReaction> reactions = physicalEntityNode.getReactions(pathwayId);
                        pathwayNode.process(mainIdentifier, reactions);
                    }
                }
            }
        }

        for (InteractorNode interactorNode : interactorsMap.values()) {
            InteractorIdentifier identifier = new InteractorIdentifier(interactorNode.getAccession());
            for (EntityNode physicalEntityNode : interactorNode.getInteractsWith()) {
                MainIdentifier mainIdentifier = physicalEntityNode.getIdentifier();
                if (mainIdentifier != null) {
                    for (Long pathwayId : physicalEntityNode.getPathwayIds()) {
                        Set<PathwayNode> pNodes = pathwayLocation.getElements(pathwayId);
                        if (pNodes == null) continue;
                        for (PathwayNode pathwayNode : pNodes) {
                            Set<AnalysisReaction> reactions = physicalEntityNode.getReactions(pathwayId);
                            pathwayNode.processInteractor(identifier, mainIdentifier, reactions);
                        }
                    }
                }
            }
        }

        hierarchyBuilder.prepareToSerialise();
    }
}