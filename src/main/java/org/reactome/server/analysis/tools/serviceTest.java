package org.reactome.server.analysis.tools;

import com.martiansoftware.jsap.*;
import org.reactome.server.graph.domain.model.Species;
import org.reactome.server.graph.service.DatabaseObjectService;
import org.reactome.server.graph.service.GeneralService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class serviceTest {

    private static Logger logger = LoggerFactory.getLogger(BuilderTool.class);

    public static void main(String[] args) throws JSAPException {

        // Parse Program Arguments
        SimpleJSAP jsap = new SimpleJSAP(BuilderTool.class.getName(), "Provides a set of tools for the pathway analysis and species comparison",
                new Parameter[]{
                        new FlaggedOption("host", JSAP.STRING_PARSER, "localhost", JSAP.NOT_REQUIRED, 'h', "host", "The neo4j host"),
                        new FlaggedOption("port", JSAP.STRING_PARSER, "7474", JSAP.NOT_REQUIRED, 'p', "port", "The neo4j port"),
                        new FlaggedOption("user", JSAP.STRING_PARSER, "neo4j", JSAP.NOT_REQUIRED, 'u', "user", "The neo4j user"),
                        new FlaggedOption("password", JSAP.STRING_PARSER, "neo4j2", JSAP.REQUIRED, 'k', "password", "The neo4j password")
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

        GeneralService genericService = ReactomeGraphCore.getService(GeneralService.class);

        String query = "MATCH (re:ReferenceEntity)<-[:referenceEntity]-(ewas:EntityWithAccessionedSequence)\n" +
                "WITH re, ewas\n" +
//                "OPTIONAL MATCH (ewas)-[:hasModifiedResidue]->(tm:TranslationalModification)-[:psiMod]->(mod:PsiMod)\n" +
                "RETURN re, ewas";

        System.out.println(genericService.query(query, new HashMap<>()));
//        System.out.println("Database name: " + genericService.getDBName());
//        System.out.println("Database version: " + genericService.getDBVersion());

//        DatabaseObjectService databaseObjectService = ReactomeGraphCore.getService(DatabaseObjectService.class);
//        Species homoSapiens = (Species) databaseObjectService.findByIdNoRelations(48887L);
//        System.out.println(homoSapiens);
    }
}
