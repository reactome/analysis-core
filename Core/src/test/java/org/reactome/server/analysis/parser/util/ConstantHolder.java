package org.reactome.server.analysis.parser.util;

import org.reactome.server.analysis.parser.InputProcessor;
import org.springframework.util.StopWatch;

import java.time.Duration;
import java.util.TreeMap;

public class ConstantHolder {
    public static final int REPETITIONS = 500;
    public static int WARMUP_OFFSET = 100;

//    public static int sizes[] = {1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000};
    public static int sizes[] = {1, 200, 400, 600, 800, 1000, 1200, 1400, 1600, 1800, 2000, 2200, 2400, 2600, 2800, 3000, 3200, 3400, 3600, 3800, 4000,
            4200, 4400, 4600, 4800, 5000, 5200, 5400, 5600, 5800, 6000, 6200, 6400, 6600, 6800, 7000, 7200, 7400, 7600, 7800, 8000,
            8200, 8400, 8600, 8800, 9000, 9200, 9400, 9600, 9800, 10000};

    public static TreeMap<String, Long> totals = new TreeMap<>();
    public static TreeMap<String, Duration[]> times = new TreeMap<>();
    public static TreeMap<String, Integer> repetitions = new TreeMap<>();

    public static final String PATH = "analysis/input/"; // Path relative to the project
    public static final String PATH_STATS = "C:\\Users\\Francisco\\Documents\\phd\\Projects\\Golden\\docs\\plots\\";


    public enum InputTypeEnum {
        maxQuantMatrix,
        peptideList,
        peptideListAndSites,
        peptideListAndModSites,
        uniprotList,
        uniprotListAndSites,
        uniprotListAndModSites,
        rsid,
        vcf,
        rsidList,
        ensemblList,
        geneList,
        peff
    }
}
