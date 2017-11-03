package org.reactome.server.analysis.tools;

import org.reactome.server.analysis.core.util.MapList;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TestProteoformsGenerator {

    private static final String ALL_REACTOME_PROTEOFORMS_PATH = "C:\\Users\\Francisco\\Documents\\phd\\Projects\\Golden\\resources\\";
    private static final String ALL_REACTOME_PROTEOFORMS_FILE = "reactomeAllProteoforms.txt";
    private static int[] sizes = {1, 10, 50, 100, 150, 200, 250, 500, 1000, 5000, 10000};
    //    private static String path = "C:\\Users\\Francisco\\Documents\\phd\\Projects\\Reactome\\AnalysisTools\\Core\\target\\test-classes\\analysis\\input\\UniProt\\";
    private static String path = "C:\\Users\\Francisco\\Documents\\phd\\Projects\\AnalysisTools\\Core\\target\\test-classes\\analysis\\input\\";

    private static final String UNIPROT_ALL_HUMAN_CURATED = ".\\target\\test-classes\\analysis\\input\\UniProt_Curated_Human\\Human20218.txt";


    private static List<String> proteins;
    private static List<String> flatProteoforms = new ArrayList<>();
    private static MapList<String, MapList<String, Long>> proteoforms;

    public static void main(String args[]) throws IOException {

        createConstants();
        createProteoformsTests();
    }

    private static void createConstants() {

        String fileName = "uniprotListAndModSites_";

        for (int I = 0; I < sizes.length; I++) {
            System.out.println("public static final String " + fileName + String.format("%06d", sizes[I]) + " = PATH.concat(\"" + fileName +  String.format("%06d", sizes[I]) + ".txt\");");
//            generateFile(sizes[I], fileName, flatProteoforms);
        }

    }

    public static void createProteinTestsAndFiles() throws IOException {
        File file = new File(path + "uniprot-all.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bf = new BufferedInputStream(fileInputStream);
        proteins = new ArrayList<>();
        String protein = readNextString(bf);
        while (protein.length() > 0) {
            proteins.add(protein);
            protein = readNextString(bf);
        }
    }

    public static void createProteoformsTests() throws IOException {

        flatProteoforms = Files.readAllLines(Paths.get(ALL_REACTOME_PROTEOFORMS_PATH, ALL_REACTOME_PROTEOFORMS_FILE), Charset.defaultCharset());
        String fileName = "uniprotListAndModSites_";

        for (int I = 0; I < sizes.length; I++) {
            writeTest(sizes[I], fileName, PARSER_TYPE.InputFormatExtended);
//            generateFile(sizes[I], fileName, flatProteoforms);
        }
    }

    private static void generateFile(int n, String fileName, List<String> rows) throws IOException {

        File file = new File(path + fileName + String.format("%06d", n) + ".txt");
        FileWriter fw = new FileWriter(file);

        int cont = 0;
        for (String row : rows) {
            fw.write(row + "\n");
            cont++;
            if (cont == n) {
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

    private static void writeTest(int size, String fileName, PARSER_TYPE parserVersion) {

        System.out.println("@RepeatedTest(REPETITIONS)");
        System.out.println("public void test" + fileName + String.format("%06d", size) + "() throws ParserException {");
        System.out.println("File file = getFileFromResources(" + fileName + String.format("%06d", size) + ");");
        System.out.println(parserVersion.toString() + " format = null;\n" +
                "        try {\n" +
                "            format = parser" + (parserVersion.equals(PARSER_TYPE.InputFormat) ? "" : "Extended") + "(file);\n" +
                "        } catch (ParserException e) {\n" +
                "            Assert.fail(" + fileName + String.format("%06d", size) + " + \" has failed.\");" +
                "        }\n");
        System.out.println("Assert.assertEquals(1, format.getHeaderColumnNames().size());");
        System.out.println("Assert.assertEquals(" + size + ", format.getAnalysisIdentifierSet().size());");
        System.out.println("Assert.assertEquals(1, format.getWarningResponses().size());");

        System.out.println("}\n\n");
    }

    private enum PARSER_TYPE {
        InputFormat,
        InputFormatExtended
    }
}
