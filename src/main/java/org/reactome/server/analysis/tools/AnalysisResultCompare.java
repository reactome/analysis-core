package org.reactome.server.analysis.tools;

import org.reactome.server.analysis.core.util.Pair;
import org.reactome.server.analysis.tools.util.ResultPathwayEntry;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

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

        pw.write("The pathways in the new analysis but not in the original.\n");
        for (Map.Entry<String, ResultPathwayEntry> entry : newAnalysis.entrySet()) {
            if (!oldAnalysis.containsKey(entry.getKey())) {
                pw.println(entry.getKey());
                newMappedPathways.add(entry.getKey());
                checkIfNewHitsArePresent(entry.getValue());
            } else {
                printEntryComparison(oldAnalysis.get(entry.getKey()), newAnalysis.get(entry.getKey()));
            }
        }

        pw.write("The pathways in the original analysis but not in the new.\n");
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

    private static void checkIfNewHitsArePresent(ResultPathwayEntry newResultPathwayEntry) {
        boolean hasSeparator = false;
        // Check Submitted entities found
        if (newResultPathwayEntry.getEntities_found() > 0) {

            if (!hasSeparator) {
                System.out.println("-----------------------" + newResultPathwayEntry.getPathway() + "------------------------");
            }
            hasSeparator = true;

            System.out.println("Check if: " + newResultPathwayEntry.getMapped_entities()
                    + "\nare at: " + newResultPathwayEntry.getFound_reaction_identifiers());
        }

        // Submitted entities hit interactor
        if (newResultPathwayEntry.getInteractors_found() > 0) {

            if (!hasSeparator) {
                System.out.println("-----------------------" + newResultPathwayEntry.getPathway() + "------------------------");
            }
            hasSeparator = true;

            System.out.println("Check if: " + newResultPathwayEntry.getSubmitted_entities_hit_interactor()
                    + "\ninteract(s) with: " + newResultPathwayEntry.getInteracts_with()
                    + "\nat: " + newResultPathwayEntry.getFound_reaction_identifiers());
        }
    }

    private static void printEntryComparison(ResultPathwayEntry oldResultPathwayEntry, ResultPathwayEntry newResultPathwayEntry) {
        //TODO: Check if extra entities are in original list and in reactions in the pathway browser

        boolean hasSeparator = false;
        if (oldResultPathwayEntry.getEntities_found() != newResultPathwayEntry.getEntities_found()) {

            if (!hasSeparator) {
                System.out.println("-----------------------" + newResultPathwayEntry.getPathway() + "------------------------");
            }
            hasSeparator = true;

            System.out.println("Entities found are different: " + oldResultPathwayEntry.getEntities_found() + " --> " + newResultPathwayEntry.getEntities_found());

            // Show diff submitted entities found
            System.out.print("Extra entities found: ");
            System.out.println(getDiff(oldResultPathwayEntry.getSubmitted_entities_found(), newResultPathwayEntry.getSubmitted_entities_found()));

            // Show diff mapped entities
            System.out.print("Extra mapped entities: ");
            System.out.println(getDiff(oldResultPathwayEntry.getMapped_entities(), newResultPathwayEntry.getMapped_entities()));
        }


        // Show diff submitted entities hit interactor
        if (oldResultPathwayEntry.getInteractors_found() != newResultPathwayEntry.getInteractors_found()) {

            if (!hasSeparator) {
                System.out.println("-----------------------" + newResultPathwayEntry.getPathway() + "------------------------");
            }
            hasSeparator = true;

            System.out.println("Interactors found are different: " + oldResultPathwayEntry.getInteractors_found() + " --> " + newResultPathwayEntry.getInteractors_found());

            // Show diff Submitted entities hit interactor
            System.out.print("Extra entities hit by interactor: ");
            System.out.println(getDiff(oldResultPathwayEntry.getSubmitted_entities_hit_interactor(), newResultPathwayEntry.getMapped_entities()));

            // Show diff interacts with
            System.out.print("Extra interactors: ");
            System.out.println(getDiff(oldResultPathwayEntry.getInteracts_with(), newResultPathwayEntry.getInteracts_with()));
        }

        // Show diff in reactions hit
        if (oldResultPathwayEntry.getReactions_found() != newResultPathwayEntry.getReactions_found()) {

            if (!hasSeparator) {
                System.out.println("-----------------------" + newResultPathwayEntry.getPathway() + "------------------------");
            }

            System.out.print("Extra reactions hit: ");
            System.out.println(getDiff(oldResultPathwayEntry.getFound_reaction_identifiers(), newResultPathwayEntry.getFound_reaction_identifiers()));
        }
    }

    /**
     * Gets a list of elements in the first list that are not in the second list
     */
    private static Set<String> getDiff(Collection<String> list1, Collection<String> list2) {
        Set<String> diff = new HashSet<>();

        for (String str1 : list1) {
            if (!list2.contains(str1))
                diff.add(str1);
        }

        return diff;
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
                if ((char) i == '\"') {
                    hasQuotes = !hasQuotes;
                } else {
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
