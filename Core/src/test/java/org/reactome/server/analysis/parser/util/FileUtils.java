package org.reactome.server.analysis.parser.util;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.common.io.*;

public class FileUtils {

    public static String getString_readAllBytes(String fileName) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(fileName));
        return new String(encoded, "UTF-8");
    }


    public static String getString_BufferedReader(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName), 8192);
        StringBuilder str = new StringBuilder();
        String line = "";
        while ((line = br.readLine()) != null) {
            str.append(line);
        }
        return str.toString();
    }

    public static String getString(String fileName){
        File file = new File(fileName);
        try {
            return com.google.common.io.Files.asCharSource(file, Charset.defaultCharset()).read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getString_channel(String fileName) {
        try {
            final FileChannel channel = new FileInputStream(fileName).getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            channel.close();
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
