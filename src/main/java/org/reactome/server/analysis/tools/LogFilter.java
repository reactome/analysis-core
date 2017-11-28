package org.reactome.server.analysis.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class LogFilter {
    public static void main(String args[]) throws IOException {
        String fileName = "C:\\Users\\Francisco\\Documents\\phd\\Projects\\Reactome\\AnalysisTools\\Core\\logs\\log.out";
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(System.out::println);
        }
    }
}
