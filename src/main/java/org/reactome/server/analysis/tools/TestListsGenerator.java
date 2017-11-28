package org.reactome.server.analysis.tools;

import java.io.*;
import java.util.*;

public class TestListsGenerator {

    private static int[] sizes = {10, 50, 100, 150, 200, 250, 500, 1000, 5000, 10000, 50000, 100000};
    private static List<String> proteins;
    private static String baseName = "uniprot";
//    private static String path = "C:\\Users\\Francisco\\Documents\\phd\\Projects\\Reactome\\AnalysisTools\\Core\\target\\test-classes\\analysis\\input\\UniProt\\";
    private static String path = "C:\\Users\\Francisco\\Documents\\phd\\Projects\\AnalysisTools\\Core\\target\\test-classes\\analysis\\input\\UniProt\\";

    public static void main(String args[]) throws IOException {

//        FileWriter analysisStressTestsFile = new FileWriter("C:\\Users\\Francisco\\Documents\\phd\\Projects\\Reactome\\AnalysisTools\\Core\\src\\test\\java\\org\\reactome\\server\\analysis\\tools\\parser\\AnalysisStressTests.java");
        FileWriter analysisStressTestsFile = new FileWriter("C:\\Users\\Francisco\\Documents\\phd\\Projects\\AnalysisTools\\Core\\src\\test\\java\\org\\reactome\\server\\analysis\\tools\\parser\\AnalysisStressTests.java");

        File file = new File(path + "uniprot-all.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bf = new BufferedInputStream(fileInputStream);
        proteins = new ArrayList<>();
        String protein = readNextString(bf);
        while (protein.length() > 0) {
            proteins.add(protein);
            protein = readNextString(bf);
        }

        writeTestFileHeader(analysisStressTestsFile);
        writeTestFileInputPaths(analysisStressTestsFile);

        for (int I = 0; I < sizes.length; I++) {
            generateFile(sizes[I]);
            writeTest(sizes[I], analysisStressTestsFile);
        }

        System.out.println(proteins.size());
        writeTestFileFooter(analysisStressTestsFile);
        analysisStressTestsFile.close();
    }

    private static void generateFile(int n) throws IOException {

        File file = new File(path + "Uniprot" + String.format("%06d", n) + ".txt");
        FileWriter fw = new FileWriter(file);

        int cont = 0;
        for (String protein : proteins) {
            fw.write(protein + "\n");
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

    private static void writeTestFileHeader(FileWriter fw) throws IOException {
        fw.write("package org.reactome.server.analysis.tools.parser;\n" +
                "\n" +
                "import org.apache.commons.io.IOUtils;\n" +
                "import org.junit.Assert;\n" +
                "import org.junit.Test;\n" +
                "import org.reactome.server.analysis.core.model.AnalysisIdentifier;\n" +
                "import org.reactome.server.analysis.parser.InputFormat;\n" +
                "import org.reactome.server.analysis.parser.exception.ParserException;\n" +
                "\n" +
                "import java.io.File;\n" +
                "import java.io.FileInputStream;\n" +
                "import java.io.IOException;\n" +
                "import java.io.InputStream;\n" +
                "import java.net.URL;\n" +
                "\n" +
                "public class AnalysisStressTests {\n" +
                "\n" +
                "    private static final String PATH = \"analysis/input/Uniprot/\";\n" +
                "    private static final String SAMPLE = PATH.concat(\"samples/\");\n\n");
    }

    private static void writeTestFileInputPaths(FileWriter fw) throws IOException {
        for (int I = 0; I < sizes.length; I++) {
            fw.write("private static final String UNIPROT_ACCESSION_LIST_" + String.format("%06d", sizes[I])
                    + " = PATH.concat(\"Uniprot" + String.format("%06d", sizes[I]) + ".txt\");\n");
        }
    }

    private static void writeTest(int size, FileWriter fw) throws IOException {
        fw.write("@Test\n");
        fw.write("public void testUniprotAccessionList" + String.format("%06d", size) + "() throws ParserException {\n");
        fw.write("long start = System.currentTimeMillis();\n");
        fw.write("File file = getFileFromResources(UNIPROT_ACCESSION_LIST_" + String.format("%06d", size) + ");\n");
        fw.write("InputFormat format = null;\n" +
                "        try {\n" +
                "            format = parser(file);\n" +
                "        } catch (ParserException e) {\n" +
                "            Assert.fail(UNIPROT_ACCESSION_LIST_" + String.format("%06d", size) + " + \" has failed.\");\n" +
                "        }\n");
        fw.write("\nAssert.assertEquals(1, format.getHeaderColumnNames().size());\n");
        fw.write("Assert.assertEquals(" + size + ", format.getAnalysisIdentifierSet().size());\n");
        fw.write("Assert.assertEquals(1, format.getWarningResponses().size());\n");

        Random randomizer = new Random();
        Set<String> selected = new HashSet<>();
        for (int I = 0; I < size && I < size * 0.1 && I < 10; I++) {
            int index = randomizer.nextInt(size);
            while (selected.contains(proteins.get(index))) {
                index = randomizer.nextInt(size);
            }
            String random = proteins.get(index);
            fw.write("Assert.assertTrue(\"Looking for " + random + "\", format.getAnalysisIdentifierSet().contains(new AnalysisIdentifier(\"" + random + "\")));");
        }

        fw.write("long end = System.currentTimeMillis();\n" +
                "        System.out.println(\"Uniprot" + String.format("%06d", size) + "\\t\" + (end - start));");
        fw.write("}\n\n");
    }

    private static void writeTestFileFooter(FileWriter fw) throws IOException {
        fw.write("\nprivate File getFileFromResources(String fileName) {\n" +
                "        String msg = \"Can't get an instance of \".concat(fileName);\n" +
                "\n" +
                "        ClassLoader classLoader = getClass().getClassLoader();\n" +
                "        if (classLoader == null) {\n" +
                "            Assert.fail(\"[1] - \".concat(msg));\n" +
                "        }\n" +
                "\n" +
                "        URL url = classLoader.getResource(fileName);\n" +
                "        if (url == null) {\n" +
                "            Assert.fail(\"[2] - \".concat(msg));\n" +
                "        }\n" +
                "\n" +
                "        File file = new File(url.getFile());\n" +
                "        if (!file.exists()) {\n" +
                "            Assert.fail(\"[3] - \".concat(msg));\n" +
                "        }\n" +
                "\n" +
                "        return file;\n" +
                "    }\n\n");

        fw.write("\nprivate InputFormat parser(File file) throws ParserException {\n" +
                "        InputFormat format = new InputFormat();\n" +
                "\n" +
                "        try {\n" +
                "            InputStream fis = new FileInputStream(file);\n" +
                "            format.parseData(IOUtils.toString(fis));\n" +
                "        } catch (IOException e) {\n" +
                "            Assert.fail(\"Couldn't get the file to be analysed properly. File [\".concat(file.getName()).concat(\"]\"));\n" +
                "        }\n" +
                "\n" +
                "        return format;\n" +
                "    }\n\n");

        fw.write("}");

    }
}
