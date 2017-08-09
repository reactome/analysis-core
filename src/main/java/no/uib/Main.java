
package no.uib;

import com.martiansoftware.jsap.*;
import org.reactome.server.graph.domain.model.*;
import org.reactome.server.graph.service.DatabaseObjectService;
import org.reactome.server.graph.service.GeneralService;
import org.reactome.server.graph.service.SchemaService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import no.uib.ReactomeNeo4jConfig;

import java.util.List;

public class Main {

    public static void main(String[] args) throws JSAPException {

        // Program Arguments -h, -p, -u, -k
        SimpleJSAP jsap = new SimpleJSAP(Main.class.getName(), "Connect to Reactome Graph Database",
                new Parameter[]{
                        new FlaggedOption("host",     JSAP.STRING_PARSER, "localhost", JSAP.NOT_REQUIRED, 'h', "host",     "The neo4j host"),
                        new FlaggedOption("port",     JSAP.STRING_PARSER, "7474",      JSAP.NOT_REQUIRED, 'p', "port",     "The neo4j port"),
                        new FlaggedOption("user",     JSAP.STRING_PARSER, "neo4j",     JSAP.NOT_REQUIRED, 'u', "user",     "The neo4j user"),
                        new FlaggedOption("password", JSAP.STRING_PARSER, "neo4j2",     JSAP.REQUIRED,     'k', "password", "The neo4j password")
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

        // Access the data using our service layer.
        GeneralService genericService = ReactomeGraphCore.getService(GeneralService.class);
        System.out.println("Database name: " + genericService.getDBName());
        System.out.println("Database version: " + genericService.getDBVersion());

        DatabaseObjectService databaseObjectService = ReactomeGraphCore.getService(DatabaseObjectService.class);
        Species homoSapiens = (Species) databaseObjectService.findByIdNoRelations(48887L);
        System.out.println(homoSapiens);

        // More services available at org.reactome.server.graph.service.*

    }
}