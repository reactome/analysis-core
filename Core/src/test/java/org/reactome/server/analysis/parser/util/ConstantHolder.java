package org.reactome.server.analysis.parser.util;

import org.springframework.util.StopWatch;

import java.time.Duration;
import java.util.TreeMap;

public class ConstantHolder {
    public static final int REPETITIONS = 1;
    public static TreeMap<String, Long> totals = new TreeMap<>();
    public static TreeMap<String, Duration[]> times = new TreeMap<>();
    public static TreeMap<String, Integer> repetitions = new TreeMap<>();

    /**
     * Input files path
     */
    private static final String PATH = "analysis/input/";
    private static final String PATH_UNIPROT = "analysis/input/UniProt/";
    public static final String PATH_PROTEOFORMS_SIMPLE = "analysis/input/ProteoformsSimple/uniprotListAndModSites_";

    /**
     * Multiple line inputs
     */
    public static final String CORRECT_FILE = PATH.concat("correct_file.txt");
    public static final String UNIPROT_ACCESSION_LIST_000010 = PATH_UNIPROT.concat("Uniprot000010.txt");
    public static final String UNIPROT_ACCESSION_LIST_000050 = PATH_UNIPROT.concat("Uniprot000050.txt");
    public static final String UNIPROT_ACCESSION_LIST_000100 = PATH_UNIPROT.concat("Uniprot000100.txt");
    public static final String UNIPROT_ACCESSION_LIST_000150 = PATH_UNIPROT.concat("Uniprot000150.txt");
    public static final String UNIPROT_ACCESSION_LIST_000200 = PATH_UNIPROT.concat("Uniprot000200.txt");
    public static final String UNIPROT_ACCESSION_LIST_000250 = PATH_UNIPROT.concat("Uniprot000250.txt");
    public static final String UNIPROT_ACCESSION_LIST_000500 = PATH_UNIPROT.concat("Uniprot000500.txt");
    public static final String UNIPROT_ACCESSION_LIST_001000 = PATH_UNIPROT.concat("Uniprot001000.txt");
    public static final String UNIPROT_ACCESSION_LIST_005000 = PATH_UNIPROT.concat("Uniprot005000.txt");
    public static final String UNIPROT_ACCESSION_LIST_010000 = PATH_UNIPROT.concat("Uniprot010000.txt");
    public static final String UNIPROT_ACCESSION_LIST_050000 = PATH_UNIPROT.concat("Uniprot050000.txt");
    public static final String UNIPROT_ACCESSION_LIST_100000 = PATH_UNIPROT.concat("Uniprot100000.txt");
}
