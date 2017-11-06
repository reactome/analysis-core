package org.reactome.server.analysis.parser.util;

import org.springframework.util.StopWatch;

import java.time.Duration;
import java.util.TreeMap;

public class ConstantHolder {
    public static final int REPETITIONS = 200;
    public static int WARMUP_OFFSET = 1;

    public static int sizes[] = {1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000};

    public static TreeMap<String, Long> totals = new TreeMap<>();
    public static TreeMap<String, Duration[]> times = new TreeMap<>();
    public static TreeMap<String, Integer> repetitions = new TreeMap<>();



    public static final String PATH = "C:\\Users\\Francisco\\Documents\\phd\\Projects\\PathwayMatcher\\target\\test-classes\\input\\";
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
