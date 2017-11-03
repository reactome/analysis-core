package org.reactome.server.analysis.tools;

import org.neo4j.ogm.model.Result;
import org.reactome.server.analysis.core.util.FrequencyEntry;
import org.reactome.server.analysis.core.util.MapList;
import org.reactome.server.graph.service.GeneralService;
import org.reactome.server.graph.utils.ReactomeGraphCore;

import java.io.*;
import java.util.*;

import static org.reactome.server.analysis.tools.ExampleFinder.getAllProteoforms;

public class StatFilesGenerator {
    private static final String FILE_REACTIONS_AND_PATHWAYS_FREQUENCIES = "./resources/Reactions_And_Pathways_Frequencies.csv";
    private static final String FILE_REACTOME_ALL_PROTEIN_IDENTIFIERS = "./resources/HumanReactomeProteins.txt";

    private static final String QUERY_GET_PROTEIN_FREQUENCIES = "MATCH (p:Pathway{speciesName:'Homo sapiens'})-[:hasEvent*]->(r:Reaction{speciesName:'Homo sapiens'})-[:input|output|catalystActivity|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate*]->(pe:PhysicalEntity)-[:referenceEntity]->(re:ReferenceEntity{identifier:{id}, databaseName:'UniProt'})\n" +
            "RETURN count(DISTINCT p) as pathways, count(DISTINCT r) as reactions";

    /**
     * The gray queries are for the plots and not for the execution of the program. Then the case of some letters changes.
     */
    private static final String QUERY_GET_ALL_PROTEIN_FREQUENCIES = "MATCH (p:Pathway{speciesName:'Homo sapiens'})-[:hasEvent*]->(r:Reaction{speciesName:'Homo sapiens'})-[:input|output|catalystActivity|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate*]->(pe:PhysicalEntity)-[:referenceEntity]->(re:ReferenceEntity{databaseName:'UniProt'})\n" +
            "RETURN DISTINCT \"Protein\" as Type, re.identifier as protein, count(DISTINCT p) as Pathways, count(DISTINCT r) as Reactions\n" +
            "ORDER BY protein";


    private static final String QUERY_GET_ALL_PROTEIN_PROTEOFORM_FREQUENCIES = "MATCH (pe:PhysicalEntity{speciesName:'Homo sapiens'})-[:referenceEntity]->(re:ReferenceEntity{databaseName:\"UniProt\"})\n" +
            "WITH DISTINCT pe, re\n" +
            "OPTIONAL MATCH (pe)-[:hasModifiedResidue]->(tm)-[:psiMod]->(mod)\n" +
            "WITH DISTINCT pe, re, tm.coordinate as coordinate, mod.identifier as type ORDER BY type, coordinate\n" +
            "WITH DISTINCT pe,re, COLLECT(CASE WHEN coordinate IS NOT NULL THEN coordinate ELSE null END + \":\" + type) AS ptms\n" +
            "WITH DISTINCT pe, re, ptms\n" +
            "MATCH (p:Pathway{speciesName:'Homo sapiens'})-[:hasEvent*]->(r:Reaction{speciesName:'Homo sapiens'})-[:input|output|catalystActivity|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate*]->(pe)\n" +
            "RETURN \"Proteoform\" as Type, CASE WHEN re.variantIdentifier IS NOT NULL THEN re.variantIdentifier ELSE re.identifier END as protein, ptms, count(DISTINCT p) as Pathways, count(DISTINCT r) as Reactions\n" +
            "ORDER BY protein, ptms";


    private static final String QUERY_GET_NOT_EMTPY_PROTEIN_FREQUENCIES = "MATCH (pe:PhysicalEntity{speciesName:'Homo sapiens'})-[:referenceEntity]->(re:ReferenceEntity{databaseName:\"UniProt\"})\n" +
            "WITH DISTINCT pe, re\n" +
            "OPTIONAL MATCH (pe)-[:hasModifiedResidue]->(tm)-[:psiMod]->(mod)\n" +
            "WITH DISTINCT pe, re, tm.coordinate as coordinate, mod.identifier as type ORDER BY type, coordinate\n" +
            "WITH DISTINCT pe,re, COLLECT(CASE WHEN coordinate IS NOT NULL THEN coordinate ELSE null END + \":\" + type) AS ptms\n" +
            "WITH DISTINCT pe, re, ptms\n" +
            "WHERE size(ptms) >= 1\n" +
            "MATCH (p:Pathway{speciesName:'Homo sapiens'})-[:hasEvent*]->(r:Reaction{speciesName:'Homo sapiens'})-[:input|output|catalystActivity|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate*]->(pe)\n" +
            "RETURN \"Protein\" as Type, re.identifier as protein, count(DISTINCT p) as Pathways, count(DISTINCT r) as Reactions\n" +
            "ORDER BY protein";

    /**
     * A __non empty proteoform_ is a proteoform with one or more ptms.
     * The average number of pathways/reactions hit by this is expected to be lower compared to the total hits by the
     * generic form of the protein, but this only applies when there are multiple proteoforms in the protein.
     * If there is only one and contains many ptms, then the number of hits is not reduced.
     */
    private static final String QUERY_GET_NON_EMPTY_PROTEOFORM_FREQUENCIES = "MATCH (pe:PhysicalEntity{speciesName:'Homo sapiens'})-[:referenceEntity]->" +
            "(re:ReferenceEntity{databaseName:\"UniProt\"})\n" +
            "WITH DISTINCT pe, re\n" +
            "OPTIONAL MATCH (pe)-[:hasModifiedResidue]->(tm)-[:psiMod]->(mod)\n" +
            "WITH DISTINCT pe, re, tm.coordinate as coordinate, mod.identifier as type ORDER BY type, coordinate\n" +
            "WITH DISTINCT pe,re, COLLECT(CASE WHEN coordinate IS NOT NULL THEN coordinate ELSE null END + \":\" + type) AS ptms\n" +
            "WITH DISTINCT pe, re, ptms\n" +
            "WHERE size(ptms) >= 1\n" +
            "MATCH (p:Pathway{speciesName:'Homo sapiens'})-[:hasEvent*]->(r:Reaction{speciesName:'Homo sapiens'})-" +
            "[:input|output|catalystActivity|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate*]->(pe)\n" +
            "RETURN \"Proteoform\" as Type, CASE WHEN re.variantIdentifier IS NOT NULL THEN re.variantIdentifier ELSE re.identifier END as protein, ptms, count(DISTINCT p) as Pathways, count(DISTINCT r) as Reactions\n" +
            "ORDER BY protein";


    /**
     * A __Modified protein__ is a protein that contains modification information. In other words, a protein with more than one proteoform.
     * The objective is to be more specific and decrease the average number of pathways/reactions hit. Since the total number
     * of hits of the protein are divided in more than one group, ideally the average should decrease.
     */
    private static final String QUERY_GET_ALL_MODIFIED_PROTEIN_FREQUENCIES = "MATCH (pe:PhysicalEntity{speciesName:'Homo sapiens'})-[:referenceEntity]->(re:ReferenceEntity{databaseName:\"UniProt\"})\n" +
            "WITH DISTINCT pe, re\n" +
            "OPTIONAL MATCH (pe)-[:hasModifiedResidue]->(tm)-[:psiMod]->(mod)\n" +
            "WITH DISTINCT pe, re, tm.coordinate as coordinate, mod.identifier as type ORDER BY type, coordinate\n" +
            "WITH DISTINCT pe,re, COLLECT(CASE WHEN coordinate IS NOT NULL THEN coordinate ELSE null END + \":\" + type) AS ptms\n" +
            "WITH DISTINCT CASE WHEN re.variantIdentifier IS NOT NULL THEN re.variantIdentifier ELSE re.identifier END as protein, count(DISTINCT {id:CASE WHEN re.variantIdentifier IS NOT NULL THEN re.variantIdentifier ELSE re.identifier END, ptms:ptms}) as numberProteoforms, collect(pe) as physicalEntities\n" +
            "WHERE numberProteoforms > 1\n" +
            "WITH protein, numberProteoforms, physicalEntities\n" +
            "UNWIND physicalEntities AS pe\n" +
            "MATCH (p:Pathway{speciesName:'Homo sapiens'})-[:hasEvent*]->(r:Reaction{speciesName:'Homo sapiens'})-[:input|output|catalystActivity|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate*]->(pe)\n" +
            "RETURN \"Modified Protein\" as Type, protein, numberProteoforms, count(DISTINCT p) as Pathways, count(DISTINCT r) as Reactions\n" +
            "ORDER BY protein";

    private static final String QUERY_GET_ALL_MODIFIED_PROTEIN_PROTEOFORM_FREQUENCIES = "MATCH (pe:PhysicalEntity{speciesName:'Homo sapiens'})-[:referenceEntity]->(re:ReferenceEntity{databaseName:\"UniProt\"})\n" +
            "WITH DISTINCT pe, re\n" +
            "OPTIONAL MATCH (pe)-[:hasModifiedResidue]->(tm)-[:psiMod]->(mod)\n" +
            "WITH DISTINCT pe, re, tm.coordinate as coordinate, mod.identifier as type ORDER BY type, coordinate\n" +
            "WITH DISTINCT pe,re, COLLECT(CASE WHEN coordinate IS NOT NULL THEN coordinate ELSE null END + \":\" + type) AS ptms\n" +
            "WITH DISTINCT CASE WHEN re.variantIdentifier IS NOT NULL THEN re.variantIdentifier ELSE re.identifier END as protein, count(DISTINCT {id:CASE WHEN re.variantIdentifier IS NOT NULL THEN re.variantIdentifier ELSE re.identifier END, ptms:ptms}) as numberProteoforms, collect(pe) as physicalEntities\n" +
            "WHERE numberProteoforms > 1\n" +
            "WITH protein, numberProteoforms, physicalEntities\n" +
            "UNWIND physicalEntities AS pe\n" +
            "OPTIONAL MATCH (pe)-[:hasModifiedResidue]->(tm)-[:psiMod]->(mod)\n" +
            "WITH DISTINCT pe, protein, tm.coordinate as coordinate, mod.identifier as type ORDER BY type, coordinate\n" +
            "WITH DISTINCT pe,protein, COLLECT(CASE WHEN coordinate IS NOT NULL THEN coordinate ELSE null END + \":\" + type) AS ptms\n" +
            "WITH DISTINCT pe, protein, ptms\n" +
            "MATCH (p:Pathway{speciesName:'Homo sapiens'})-[:hasEvent*]->(r:Reaction{speciesName:'Homo sapiens'})-[:input|output|catalystActivity|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate*]->(pe)\n" +
            "RETURN \"Modified Protein Proteoform\" as Type, protein, ptms, count(DISTINCT p) as Pathways, count(DISTINCT r) as Reactions\n" +
            "ORDER BY protein, ptms";

    /**
     * Individual protein queries
     */
    private static final String QUERY_GET_PROTEIN_PROTEOFORM_FREQUENCIES = "MATCH (pe:PhysicalEntity{speciesName:'Homo sapiens'})-[:referenceEntity]->(re:ReferenceEntity{databaseName:\"UniProt\"})\n" +
            "WITH DISTINCT pe, re\n" +
            "OPTIONAL MATCH (pe)-[:hasModifiedResidue]->(tm)-[:psiMod]->(mod)\n" +
            "WITH DISTINCT pe, re, tm.coordinate as coordinate, mod.identifier as type ORDER BY type, coordinate\n" +
            "WITH DISTINCT pe,re, COLLECT(CASE WHEN coordinate IS NOT NULL THEN coordinate ELSE null END + \":\" + type) AS ptms\n" +
            "WITH DISTINCT pe, re, ptms\n" +
            "MATCH (p:Pathway{speciesName:'Homo sapiens'})-[:hasEvent*]->(r:Reaction{speciesName:'Homo sapiens'})-[:input|output|catalystActivity|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate*]->(pe)\n" +
            "RETURN \"Proteoform\" as Type, re.identifier as protein, re.variantIdentifier as isoform, ptms, count(DISTINCT p) as Pathways, count(DISTINCT r) as Reactions";

    private static final String QUERY_GET_PROTEOFORM_FREQUENCIES = "MATCH (re:ReferenceEntity{identifier:{id},databaseName:'UniProt'})<-[:referenceEntity]-(pe:PhysicalEntity{speciesName:'Homo sapiens'})\n" +
            "WITH re, pe\n" +
            "OPTIONAL MATCH (pe)-[modified:hasModifiedResidue]->(tm:TranslationalModification)-[:psiMod]->(mod:PsiMod)\n" +
            "WITH DISTINCT re, pe, CASE WHEN modified IS NULL THEN [] ELSE collect({coordinate: tm.coordinate, type: mod.identifier}) END as ptms\n" +
            "WHERE size(ptms) = {numPtms} AND all(ptm in {listPtms} WHERE ptm in ptms)\n" +
            "WITH re, pe, ptms\n" +
            "MATCH (p:Pathway{speciesName:'Homo sapiens'})-[:hasEvent*]->(r:Reaction{speciesName:'Homo sapiens'})-[:input|output|catalystActivity|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate*]->(pe)\n" +
            "RETURN re.identifier, re.variantIdentifier, count(DISTINCT p) as pathways, count(DISTINCT r) as reactions";
    private static final String QUERY_GET_NUMBER_PROTOFORMS_PER_PROTEIN = "MATCH (pe:PhysicalEntity{speciesName:'Homo sapiens'})-[:referenceEntity]->(re:ReferenceEntity{databaseName:\"UniProt\"})\nWITH DISTINCT pe, re\nOPTIONAL MATCH (pe)-[:hasModifiedResidue]->(tm)-[:psiMod]->(mod)\nWITH DISTINCT pe, re, tm.coordinate as coordinate, mod.identifier as type ORDER BY type, coordinate\nWITH DISTINCT pe,re, COLLECT(CASE WHEN coordinate IS NOT NULL THEN coordinate ELSE null END + \":\" + type) AS ptms\nRETURN re.identifier, size(collect(DISTINCT {protein: re.identifier, isoform: re.variantIdentifier, ptms: ptms})) as proteoforms";

    @Deprecated
    private static final String QUERY_GET_PROTEOFORM_FREQUENCIES_VARIANT_IDENTIFIER = "MATCH (re:ReferenceEntity{databaseName:'UniProt'})<-[:referenceEntity]-(pe:PhysicalEntity{speciesName:'Homo sapiens'})\n" +
            "WHERE re.variantIdentifier = {id}" +
            "WITH pe\n" +
            "OPTIONAL MATCH (pe)-[modified:hasModifiedResidue]->(tm:TranslationalModification)-[:psiMod]->(mod:PsiMod)\n" +
            "WITH DISTINCT pe, CASE WHEN modified IS NULL THEN [] ELSE collect({coordinate: tm.coordinate, type: mod.identifier}) END as ptms\n" +
            "WHERE size(ptms) = {numPtms} AND all(ptm in {listPtms} WHERE ptm in ptms)\n" +
            "WITH pe, ptms\n" +
            "MATCH (p:Pathway{speciesName:'Homo sapiens'})-[:hasEvent*]->(r:Reaction{speciesName:'Homo sapiens'})-[:input|output|catalystActivity|physicalEntity|regulatedBy|regulator|hasComponent|hasMember|hasCandidate*]->(pe)\n" +
            "RETURN count(DISTINCT p) as pathways, count(DISTINCT r) as reactions";

    public static void main(String args[]) throws IOException {
        createReactionsAndPathwaysFrequenciesFile();
    }

    private static void createReactionsAndPathwaysFrequenciesFile() throws IOException {
        // Get table of frequencies for Proteins
        File file = new File(FILE_REACTOME_ALL_PROTEIN_IDENTIFIERS);
        FileInputStream fileInputStreamUniprot = new FileInputStream(file);
        BufferedInputStream bfUniprot = new BufferedInputStream(fileInputStreamUniprot);
        Set<String> proteins = new TreeSet<>();
        FileWriter resultFile = new FileWriter(FILE_REACTIONS_AND_PATHWAYS_FREQUENCIES);
        List<FrequencyEntry> result = new ArrayList<>();
        String line;

        ReactomeGraphCore.initialise("localhost", "7474", "neo4j", "neo4j2", ReactomeNeo4jConfig.class);

        while ((line = readNextLine(bfUniprot)).length() > 0) {
            proteins.add(line);
        }
        for (String protein : proteins) {
            FrequencyEntry frequencyEntry = getFrequencies(protein);
            resultFile.write(frequencyEntry.toString() + "\n");
        }

        // Get table of frequencies for Proteoforms
        MapList<String, MapList<String, Long>> proteoforms = getAllProteoforms();

        for (String protein : proteoforms.keySet()) {
            for (MapList<String, Long> proteoform : proteoforms.getElements(protein)) {
                FrequencyEntry frequencyEntry = getFrequencies(protein, proteoform);
                resultFile.write(frequencyEntry.toString() + "\n");
            }
        }

        resultFile.close();
    }

    public static FrequencyEntry getFrequencies(String protein, MapList<String, Long> proteoform) {
        System.out.println("Getting frequencies for : " + protein + ";" + proteoform);
        GeneralService genericService = ReactomeGraphCore.getService(GeneralService.class);
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("id", protein);
        parameters.put("numPtms", proteoform.expandedSize());
        Result result = genericService.query(QUERY_GET_PROTEOFORM_FREQUENCIES.replace("{listPtms}", flattenProteoform(proteoform)), parameters);

        for (Map<String, Object> entry : result) {

            if (!protein.contains("-")) {
                if (entry.get("re.variantIdentifier") != null) {
                    continue;
                }
            } else {
                if (entry.get("re.variantIdentifier") == null) {
                    continue;
                }
            }

            long pathways = 0;
            if (entry.get("pathways") != null) {
                pathways = Long.valueOf((int) entry.get("pathways"));
            }
            long reactions = 0;
            if (entry.get("reactions") != null) {
                reactions = Long.valueOf((int) entry.get("reactions"));
            }
            return new FrequencyEntry(protein + ";" + flattenProteoform(proteoform), "proteoform", reactions, pathways);
        }
        return null;
    }

    private static FrequencyEntry getFrequencies(String protein) {
        GeneralService genericService = ReactomeGraphCore.getService(GeneralService.class);
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("id", protein);
        Result result = genericService.query(QUERY_GET_PROTEIN_FREQUENCIES, parameters);
        for (Map<String, Object> entry : result) {
            System.out.println(protein);
            long pathways = 0;
            if (entry.get("pathways") != null) {
                pathways = Long.valueOf((int) entry.get("pathways"));
            }
            long reactions = 0;
            if (entry.get("reactions") != null) {
                reactions = Long.valueOf((int) entry.get("reactions"));
            }
            return new FrequencyEntry(protein, "protein", reactions, pathways);
        }
        return null;
    }

    private static String flattenProteoform(MapList<String, Long> ptms) {
        StringBuilder result = new StringBuilder();
        for (String key : ptms.keySet()) {
            for (Long value : ptms.getElements(key)) {
                result.append(",{" + ptms.getValueLabel() + ":" + (value == null ? "null" : value.toString()) + "," + ptms.getKeyLabel() + ":\"" + key + "\"}");
            }
        }
        return "[" + (result.toString().length() > 0 ? result.toString().substring(1) : "") + "]";
    }

    private static String readNextLine(BufferedInputStream bf) throws IOException {
        StringBuilder line = new StringBuilder();
        int c;
        while ((c = bf.read()) != -1) {
            if (c == '\n') {
                break;
            }
            line.append((char) c);
        }
        return line.toString();
    }
}
