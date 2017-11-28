package org.reactome.server.analysis.parser;

import com.google.common.base.Stopwatch;
import org.reactome.server.analysis.parser.exception.ParserException;

import java.io.*;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.*;

import static org.reactome.server.analysis.parser.tools.ParserFactory.createParser;

public class InputPerformanceTest {

    public static final int REPETITIONS = 2;   //Number of times a specific test is run
    public static final int SAMPLE_SETS = 2;   //Number of random sample sets to be used
    public static final int WARMUP_OFFSET = 1;  // Number of runs for the warm up

    //    public static int SIZES[] = {1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000};
    public static int SIZES[] = {1, 200, 400};

    public static final String ALL_PROTEINS = "../../Golden/resources/ReactomeAllProteins.csv";
    public static final String ALL_PROTEOFORMS_SIMPLE = "../../Golden/resources/ReactomeAllProteoformsSimple.txt";
    public static final String ALL_PROTEOFORMS_PRO = "../../Golden/resources/ReactomeAllProteoformsPRO.txt";

    public static void main(String args[]) throws IOException, ParserException {

        Map<String, String> sources = new TreeMap<>();
        FileWriter timesFile = new FileWriter("Times.csv");
        Stopwatch stopwatch = Stopwatch.createUnstarted();
        PrintWriter output = new PrintWriter(System.out);

        timesFile.write("Type,Sample,Size,ms,Repetition\n");

        sources.put("Protein", ALL_PROTEINS);
        sources.put("ProteoformSimple", ALL_PROTEOFORMS_SIMPLE);
        sources.put("ProteoformPRO", ALL_PROTEOFORMS_PRO);

        for (String source : sources.keySet()) {

            List<String> allElementsList = getFileAsList(sources.get(source));      //Read the total possible entities and put it in an array

            for (int T = 0; T < SAMPLE_SETS; T++) {

                Collections.shuffle(allElementsList);           // Shuffle full list to get sample
                String[] allElements = new String[allElementsList.size()];
                allElements = allElementsList.toArray(allElements);

                int numElementsAdded = 0;
                StringBuilder input = new StringBuilder();
                for (int size : SIZES) {                        // Perform warmup runs

                    while (numElementsAdded < size) {           // Create input of desired size
                        input.append(allElements[numElementsAdded++] + "\n");
                    }

                    for (int R = 0; R < WARMUP_OFFSET; R++) {   // Performed warm up runs
                        System.out.println("Warming up:\t" + source + "\t Sample: " + T + "\t" + "Size: " + size);
                        Parser p = createParser(input.toString());
                        p.parseData(input.toString());
                    }
                    for (int R = 0; R < REPETITIONS; R++) {     // Performed timed runs

                        System.out.println("Running:\t" + source + "\t Sample: " + T + "\t" + "Size: " + size);

                        stopwatch.start();

                        Parser p = createParser(input.toString());
                        p.parseData(input.toString());

                        stopwatch.stop();
                        Duration duration = stopwatch.elapsed();
                        stopwatch.reset();

                        timesFile.write(source + "," + T + "," + size + "," + new DecimalFormat("#0.000").format(duration.toNanos() / 1000000.0) + "," + R + "\n");
                        timesFile.flush();
                    }
                }
            }
        }

        timesFile.close();
    }

    public static List<String> getFileAsList(String fileName) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(fileName), 8192);
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        br.close();
        return lines;
    }
}