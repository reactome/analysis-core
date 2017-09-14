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

    public static boolean TEST_MAIN_SPECIES;
    public static final String MAIN_SPECIES_TAX_ID = "9606";
    public static boolean VERBOSE;

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
                        , new QualifiedSwitch("test", JSAP.BOOLEAN_PARSER, null, JSAP.NOT_REQUIRED, 't', "test",
                        "Requests verbose output")
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

        TEST_MAIN_SPECIES = config.getBoolean("test");
        VERBOSE = config.getBoolean("verbose");

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
        interactorsBuilder.build(hierarchyBuilder.getHierarchies().keySet(), entitiesBuilder.getEntitiesContainer(), interactorsDatabase);

        calculateNumbersInHierarchyNodesForMainResources(hierarchyBuilder, entitiesBuilder, interactorsBuilder);

        DataContainer container = new DataContainer(hierarchyBuilder.getHierarchies(),
                hierarchyBuilder.getPathwayLocation(),
                entitiesBuilder.getEntitiesContainer(),
                entitiesBuilder.getEntitiesMap(),
                interactorsBuilder.getInteractorsMap());
        AnalysisDataUtils.kryoSerialisation(container, fileName);
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
            MapSet<Long, AnalysisReaction> pathwayReactions = interactorNode.getPathwayReactions();
            for (MainIdentifier mainIdentifier : interactorNode.getInteractsWith()) {
                for (Long pathwayId : pathwayReactions.keySet()) {
                    Set<AnalysisReaction> reactions = pathwayReactions.getElements(pathwayId);
                    Set<PathwayNode> pNodes = pathwayLocation.getElements(pathwayId);
                    for (PathwayNode pNode : pNodes) {
                        pNode.processInteractor(identifier, mainIdentifier, reactions);
                    }
                }
            }
        }

        hierarchyBuilder.prepareToSerialise();
    }
}