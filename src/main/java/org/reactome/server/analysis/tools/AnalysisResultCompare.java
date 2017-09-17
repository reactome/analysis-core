package org.reactome.server.analysis.tools;

import org.reactome.server.analysis.core.util.Pair;
import org.reactome.server.analysis.tools.util.ResultPathwayEntry;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AnalysisResultCompare {

    private static final char SEPARATOR = ',';

    private static Set<String> inputList = new HashSet<>();

    private static HashMap<String, ResultPathwayEntry> oldAnalysis;   // Key: pathway stId
    private static HashMap<String, ResultPathwayEntry> newAnalysis;   // Key: pathway stId
    private static Set<String> newMappedPathways;
    private static Set<String> previousMappedPathways;

    /**
     * @param args args[0] = inputList, args[1] = oldAnalysis, args[2] = newAnalysis
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {

        if (args.length < 3)
            return;

        PrintWriter pw = new PrintWriter(System.out);

        newMappedPathways = new HashSet<>();
        previousMappedPathways = new HashSet<>();

        // Read input list
        inputList = readInputList(args[0]);

        // Read the original results
        oldAnalysis = readAnalysisResult(args[1]);

        // Read the new results
        newAnalysis = readAnalysisResult(args[2]);

        // Find the pathways not mapped

        pw.write("The pathways in the new analysis but not in the original.");
        for (Map.Entry<String, ResultPathwayEntry> entry : newAnalysis.entrySet()) {
            if (!oldAnalysis.containsKey(entry.getKey())) {
                pw.println(entry.getKey());
                newMappedPathways.add(entry.getKey());
            }
        }

        pw.write("The pathways in the original analysis but not in the new.");
        for (Map.Entry<String, ResultPathwayEntry> entry : oldAnalysis.entrySet()) {
            if (!newAnalysis.containsKey(entry.getKey())) {
                pw.println(entry.getKey());
                previousMappedPathways.add(entry.getKey());
            }
        }

        //To each new mapped pathway, check if there are more entities mapped

        // To each different pathway check if there are more interactors mapped
        // Get the pathway --> reaction --> protein --> interactor [corrected,missing]

        pw.close();
    }

    private static Set<String> readInputList(String path) throws IOException {
        HashSet<String> inputList = new HashSet<>();
        StringBuilder str = new StringBuilder();
        BufferedInputStream bf = new BufferedInputStream(new FileInputStream(path));
        int i;
        while ((i = bf.read()) != -1) {
            char c = (char) i;
            if (c == '\t' || c == ' ' || c == '\r') {
                continue;
            } else if (c == '\n') {
                inputList.add(str.toString());
                str = new StringBuilder();
            } else {
                str.append(c);
            }
        }
        if (str.length() > 0) {
            inputList.add(str.toString());
        }
        return inputList;
    }

    private static HashMap<String, ResultPathwayEntry> readAnalysisResult(String path) throws IOException {
        BufferedInputStream bf = new BufferedInputStream(new FileInputStream(path));
        Pair<String, Boolean> field = readNextField(bf);
        ResultPathwayEntry entry = null;
        int col = 0;
        HashMap<String, ResultPathwayEntry> analysis = new HashMap<>();

        while (!field.getSnd()) {
            field = readNextField(bf);
        }

        field = readNextField(bf);
        while (field.getFst().length() > 0) {   //Read they file while there are fields
            if (col == 0) {
                entry = new ResultPathwayEntry();
            }
            entry.fillAttribute(col, field.getFst());

            if (field.getSnd()) {
                analysis.put(entry.getPathway(), entry);
                col = 0;
            } else {
                col++;
            }

            // Prepare next iteration
            field = readNextField(bf);
        }
        bf.close();
        return analysis;
    }

    /**
     * Reads the next column value
     *
     * @param bf
     * @return A pair which contains the string value of the field as fst and if it is at the end of the line as snd
     * @throws IOException
     */
    private static Pair<String, Boolean> readNextField(BufferedInputStream bf) throws IOException {
        int i;
        boolean isLastField = false;
        boolean hasQuotes = false;
        StringBuilder str = new StringBuilder();
        while ((i = bf.read()) != -1) {       //Skip all separator or line break characters before the actual content of the field
            if (i != '\n' && i != SEPARATOR) {
                if((char) i == '\"'){
                    hasQuotes = !hasQuotes;
                }else{
                    str.append((char) i);
                }
                break;
            }
        }
        while ((i = bf.read()) != -1) {       // Read the content of the field
            char c = (char) i;
            if (c == '\"') {
                hasQuotes = !hasQuotes;
            }
            if (c == '\n') {
                isLastField = true;
                break;
            } else if (i == SEPARATOR) {
                if (!hasQuotes)
                    break;
                else
                    str.append((char) i);
            } else {
                str.append((char) i);
            }
        }
        return new Pair<String, Boolean>(str.toString(), isLastField);
    }
}
