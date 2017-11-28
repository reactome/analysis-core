
package org.reactome.server.analysis.tools;

import com.martiansoftware.jsap.*;
import org.reactome.server.analysis.tools.components.AnalysisBuilder;
import org.reactome.server.analysis.tools.util.FileUtil;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuilderTool {

    private static Logger logger = LoggerFactory.getLogger(BuilderTool.class);
    public static boolean VERBOSE;

    public static void main(String[] args) throws JSAPException {

        // Parse Program Arguments
        SimpleJSAP jsap = new SimpleJSAP(BuilderTool.class.getName(), "Provides a set of tools for the pathway analysis and species comparison",
                new Parameter[]{
                        new FlaggedOption("host", JSAP.STRING_PARSER, "localhost", JSAP.NOT_REQUIRED, 'h', "host", "The neo4j host"),
                        new FlaggedOption("port", JSAP.STRING_PARSER, "7474", JSAP.NOT_REQUIRED, 'p', "port", "The neo4j port"),
                        new FlaggedOption("user", JSAP.STRING_PARSER, "neo4j", JSAP.NOT_REQUIRED, 'u', "user", "The neo4j user"),
                        new FlaggedOption("password", JSAP.STRING_PARSER, "neo4j2", JSAP.REQUIRED, 'k', "password", "The neo4j password"),
                        new FlaggedOption("output", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, 'o', "output", "The file where the results are written to"),
                        new QualifiedSwitch( "verbose", JSAP.BOOLEAN_PARSER, null, JSAP.NOT_REQUIRED, 'v', "verbose", "Requests verbose output")
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

        String fileName = config.getString("output");
        FileUtil.checkFileName(fileName);

        VERBOSE = config.getBoolean("verbose");

        logger.info("Starting the data container creation...");
        long start = System.currentTimeMillis();

        AnalysisBuilder builder = new AnalysisBuilder();
        builder.build(fileName);

        long end = System.currentTimeMillis();
        logger.info(String.format("Data container creation finished in %d minutes", Math.round((end - start) / 60000L)));

    }
}