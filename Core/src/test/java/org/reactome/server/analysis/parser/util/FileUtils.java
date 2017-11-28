package org.reactome.server.analysis.parser.util;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.common.io.*;

public class FileUtils {

    public static String getString(String fileName){
        File file = new File(fileName);
        try {
            return com.google.common.io.Files.asCharSource(file, Charset.defaultCharset()).read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
