package org.reactome.server.analysis.parser.util;

import java.time.Duration;
import java.util.TreeMap;

public class ConstantHolder {

    public static final String PATH = "analysis/input/"; // Path relative to the project
    public static final String PATH_STATS = "C:\\Users\\Francisco\\Documents\\phd\\Projects\\Golden\\docs\\data\\";
    public static final String ALL_PROTEINS = "C:\\Users\\Francisco\\Documents\\phd\\Projects\\Golden\\resources\\HumanReactomeProteins.txt";
    public static final String ALL_PROTEOFORMS_SIMPLE = "C:\\Users\\Francisco\\Documents\\PhD\\Projects\\Golden\\resources\\ReactomeAllProteoforms.txt";
    public static final String ALL_PROTEOFORMS_PRO = "src\\test\\resources\\analysis\\input\\ProteoformsPRO\\reactomeAllProteoformsPRO.txt";

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
