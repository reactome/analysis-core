package org.reactome.server.analysis.parser.tools;

import org.reactome.server.analysis.core.model.Proteoform;
import org.reactome.server.analysis.parser.Parser;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.reactome.server.analysis.parser.tools.ProteoformsProcessor.checkForProteoforms;

public class ProteoformFormatConverter {

    public static void main(String args[]) throws IOException {
        // Read a file

        Path filePath = Paths.get("resources", "ReactomeAllProteoformsPRO.txt");
        FileWriter outFile = new FileWriter("C:\\Users\\Francisco\\Documents\\phd\\Projects\\Golden\\resources\\ReactomeAllProteoformsCopy.txt");
        List<String> lines = Files.readAllLines(filePath, Charset.defaultCharset());

        // Convert each proteoform
        for(String line : lines){
            Proteoform proteoform = ProteoformsProcessor.getProteoform(line);

            if(proteoform != null){
                String str = proteoform.toString(Parser.ProteoformFormat.PRO);
                outFile.write(str + "\n");
            }
        }
        outFile.close();
    }
}
