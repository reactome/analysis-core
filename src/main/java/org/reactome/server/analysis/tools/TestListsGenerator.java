package org.reactome.server.analysis.tools;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestListsGenerator {

    private static List<String> proteins;
    private static String baseName = "uniprot";
    private static String path = "C:\\Users\\Francisco\\Documents\\phd\\Projects\\AnalysisTools\\Core\\target\\test-classes\\analysis\\input\\UniProt_Curated_Human\\";

    public static void main(String args[]) throws IOException {

        File file = new File(path + "uniprot-all.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bf = new BufferedInputStream(fileInputStream);
        proteins = new ArrayList<>();
        String protein = readNextString(bf);
        while (protein.length() > 0) {
            proteins.add(protein);
            protein = readNextString(bf);
        }

        int[] sizes = {10,100,250,500,1000,5000,10000,50000,100000};
        for(int I = 0; I < sizes.length; I++){
            generateFile(sizes[I]);
        }

        System.out.println(proteins.size());
    }

    private static void generateFile(int n) throws IOException{

        File file = new File(path + "Uniprot" +String.format("%06d", n) + ".txt");
        FileWriter fw = new FileWriter(file);

        int cont = 0;
        for(String protein : proteins){
            fw.write(protein + "\n");
            cont++;
            if(cont == n){
                break;
            }
        }

        fw.close();
    }

    private static String readNextString(BufferedInputStream bf) throws IOException {
        int i;
        StringBuilder str = new StringBuilder();

        while ((i = bf.read()) != -1) {
            if (i == '\n') {
                break;
            }
            str.append((char) i);
        }
        return str.toString();
    }
}
