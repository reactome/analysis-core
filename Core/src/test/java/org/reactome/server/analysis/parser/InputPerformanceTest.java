package org.reactome.server.analysis.parser;

import com.google.common.base.Stopwatch;
import org.reactome.server.analysis.parser.exception.ParserException;
import org.reactome.server.analysis.parser.util.ConstantHolder.*;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import static org.reactome.server.analysis.parser.util.ConstantHolder.*;
import static org.reactome.server.analysis.parser.util.FileUtils.getString_channel;

public class InputPerformanceTest {

    public static void main(String args[]) throws IOException, ParserException {

        Set<InputTypeEnum> typesToTest = new HashSet<>();
        Set<Parser> processorVersions = new HashSet<>();
        FileWriter timesFile = new FileWriter(PATH_STATS + "Times.csv");
        Stopwatch stopwatch = Stopwatch.createUnstarted();

//        typesToTest.add(InputTypeEnum.uniprotList);
        typesToTest.add(InputTypeEnum.uniprotListAndModSites);

        processorVersions.add(new ParserExtended());
//        processorVersions.add(new ParserOriginal());

        timesFile.write("Version,Test,Size,ms,Repetition\n");

        for (Parser p : processorVersions) {
            for (InputTypeEnum t : typesToTest) {
                for (int s : sizes) {
                    for (int w = 0; w < WARMUP_OFFSET; w++) {
                        String input = getString_channel(PATH + t + "_" + String.format("%05d", s) + ".txt");
                        p.parseData(input);
                    }
                }
                for (int s : sizes) {
                    for (int r = 0; r < REPETITIONS; r++) {
                        String input = getString_channel(PATH + t + "_" + String.format("%05d", s) + ".txt");
                        stopwatch.start();

                        p.parseData(input);

                        stopwatch.stop();
                        Duration duration = stopwatch.elapsed();
                        // Write: Version, input type, size, time
                        timesFile.write(p.getClass().getSimpleName() + "," + t + "," + s + "," + new DecimalFormat("#0.000").format(duration.toNanos() / 1000000.0) + "," + r + "\n");
                        timesFile.flush();
//                        System.out.println(p.getClass().getSimpleName() + "," + t + "," + s + "," + new DecimalFormat("#0.000").format(duration.toNanos() / 1000000.0) + "," + r);
                        stopwatch.reset();
                    }
                }
            }
        }

        timesFile.close();
    }
}