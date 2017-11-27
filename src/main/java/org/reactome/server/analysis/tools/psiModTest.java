/*
 * Copyright 2017 Luis Francisco Hernández Sánchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.reactome.server.analysis.tools;

import com.martiansoftware.jsap.*;
import org.reactome.server.analysis.core.model.OnthologyGraph;

/**
 * @author Luis Francisco Hernández Sánchez
 */
public class psiModTest {

    public static void main(String args[]) throws JSAPException {
        // Define and parse command line options
        SimpleJSAP jsap = new SimpleJSAP(BuilderTool.class.getName(), "Provides a set of tools for the pathway analysis and species comparison",
                new Parameter[]{
                        new FlaggedOption("host", JSAP.STRING_PARSER, "localhost", JSAP.NOT_REQUIRED, 'h', "host", "The neo4j host"),
                        new FlaggedOption("port", JSAP.STRING_PARSER, "7474", JSAP.NOT_REQUIRED, 'p', "port", "The neo4j port"),
                        new FlaggedOption("user", JSAP.STRING_PARSER, "neo4j", JSAP.NOT_REQUIRED, 'u', "user", "The neo4j user"),
                        new FlaggedOption("password", JSAP.STRING_PARSER, "neo4j2", JSAP.NOT_REQUIRED, 'k', "password", "The neo4j password"),
                        new FlaggedOption("id", JSAP.STRING_PARSER, "00125", JSAP.REQUIRED, 'm', "psimod", "The psimod to search"),
                        new FlaggedOption("relation", JSAP.STRING_PARSER, "parents", JSAP.NOT_REQUIRED, 'r', "relation", "The relation to other psimods"),
                }
        );

        JSAPResult config = jsap.parse(args);
        if (jsap.messagePrinted()) System.exit(1);

        //String json = OnthologyHttpClient.getRelatedTerms("mod", config.getString("id"), config.getString("relation"));

//        // Access EBI fot the onthology to get the mod descendants
//        List<Term> modList = OnthologyHttpClient.getRelatedTerms(config.getString("id"), config.getString("relation"));
//        for (Term mod : modList) {
//            System.out.println(mod);
//        }

        // Verify the structure of the hierarchy: Rooted graph, general graph

        // 1. The whole graph shoould be accessible from the root node
        // 2. The subgraph starting from each of the child nodes of the root, must be trees (no cycles and connectec)

        // Get complete onthology hierarchy graph
        OnthologyGraph G = new OnthologyGraph();

        G.addTerm("00046");
        G.addTerm("01149");
        G.addTerm("00798");

        System.out.println("-------------00912");
        for(String parent : G.getAncestorsOf("00912")){
            System.out.println(parent);
        }

        System.out.println("-------------01875");

        for(String parent : G.getAncestorsOf("01875")){
            System.out.println(parent);
        }

        System.out.println("-------------00798");

        for(String parent : G.getAncestorsOf("00798")){
            System.out.println(parent);
        }

        System.out.println("-------------00046");

        for(String parent : G.getAncestorsOf("00046")){
            System.out.println(parent);
        }
    }
}
