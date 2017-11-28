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
package org.reactome.server.analysis.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xml.internal.utils.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.reactome.server.analysis.core.model.Term;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Luis Francisco Hernández Sánchez
 */
public class OnthologyHttpClient {

    private static int onthologySize = 20;
    private static String pattern = "obo/MOD_"; //Pattern to find the ids of the mods in the Json file of the web service.

    public static void main(String[] args) {
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet(createUriString("mod", "00125", "hierarchicalAncestors"));
            getRequest.addHeader("accept", "application/json");

            HttpResponse response = httpClient.execute(getRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed to get the PSIMOD onthology : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            String output;
            FileWriter fw = new FileWriter("./mod.json");
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                fw.write(output);
            }

            fw.close();
            httpClient.close();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getOntologyDescription(String onthologyName) {

        String uri = createUri(onthologyName);
        BufferedReader br = getContentBufferedReader(uri);
        String queryContent = convertBufferedReader(br);

        return queryContent;
    }

    private static String convertBufferedReader(BufferedReader br) {
        String result = "";
        String line;
        try {
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (IOException ex) {
            Logger.getLogger(OnthologyHttpClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    private static BufferedReader getContentBufferedReader(String uri) {
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet(uri);
            getRequest.addHeader("accept", "application/json");

            HttpResponse response = httpClient.execute(getRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed to quety the PSIMOD onthology : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            httpClient.close();

            return br;

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Consults the web service at the Onthology Lookup Service (http://www.ebi.ac.uk/ols/docs/api) to get the other PSIMOD terms with the specified relation
     *
     * @param id       The PSIMOD id for the term. Ex. "00046"
     * @param relation Any value of the enum {@link relations} Ex. "parents", "children", "hierarchicalChildren", "hierarchicalAncestors"
     * @return List
     */
    public static List<Term> getRelatedTerms(String id, String relation) {
        List<Term> result = new ArrayList<>();
        StringBuilder json = new StringBuilder();
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet getRequest = new HttpGet(createUriString("mod", id, relation));
            getRequest.addHeader("accept", "application/json");

            HttpResponse response = httpClient.execute(getRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed to get the PSIMOD onthology : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            String output;
            while ((output = br.readLine()) != null) {
                json.append(output);
            }

            httpClient.close();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json.toString());

            JsonNode root = rootNode.path("_embedded");
            Iterator<JsonNode> terms = root.elements();
            root = terms.next();
            terms = root.elements();
            while (terms.hasNext()) {
                JsonNode termNode = terms.next();
                if (termNode.get("obo_id") != null) {
                    String obo_id = termNode.get("obo_id").toString().replaceAll("\"", "");
                    String patternString = "^MOD:\\d{5}$";

                    if (obo_id.matches(patternString)) {
                        //System.out.println(obo_id);

                        obo_id = obo_id.split(":")[1];
                        Term relative = new Term(obo_id);
                        result.add(relative);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String createUri(String onthologyName) {
        String result = "http://www.ebi.ac.uk/ols/api/ontologies/" + onthologyName;
        return result;
    }

    private static String createUriString(String onthologyName, String term, String relation) {
        String result = "";
        try {
            result = "http://www.ebi.ac.uk/ols/api/ontologies/" + onthologyName + "/terms/" + URLEncoder.encode(URLEncoder.encode("htp://purl.obolibrary.org/obot/MOD_" + term, "ISO-8859-1"), "ISO-8859-1") + "/" + relation + "?size=" + onthologySize;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static int getOntologySize(String onthologyName) {
        String queryContent = getOntologyDescription(onthologyName);

        int startIndex = queryContent.indexOf("numberOfTerms") + 17;
        int endIndex = queryContent.indexOf(",", startIndex);
        String substr = queryContent.substring(startIndex, endIndex);
        int result = Integer.valueOf(substr);

        return result;
    }

    private enum relations {
        self, parents, hierarchicalParents, hierarchicalAncestors, ancestors, children, hierarchicalChildren, hierarchicalDescendants, descendants, jstree
    }
}
