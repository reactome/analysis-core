package org.reactome.server.analysis.parser.util;

import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.logging.Logger;

import static org.reactome.server.analysis.parser.util.ConstantHolder.*;
import static org.reactome.server.analysis.parser.util.Statistics.getMean;
import static org.reactome.server.analysis.parser.util.Statistics.getStandardDeviation;

public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback, AfterAllCallback {

    private static final Logger logger = Logger.getLogger(TimingExtension.class.getName());

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        getStore(extensionContext).put(extensionContext.getRequiredTestMethod(), stopwatch);
    }

    /**
     * After each test registers the time for the last 75% of executions, in order to ignore te warm-up runs.
     *
     * @param extensionContext
     * @throws Exception
     */
    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        Method testMethod = extensionContext.getRequiredTestMethod();

        Stopwatch stopwatch = getStore(extensionContext).remove(testMethod, Stopwatch.class);
        stopwatch.stop();
        Duration duration = stopwatch.elapsed();

        System.out.println(testMethod + ": " + stopwatch);

        stopwatch.reset();

//        logger.info(() -> String.format("Method [%s] took %s ms.", testMethod.getName(), duration));

        repetitions.putIfAbsent(testMethod.getName(), 0);
        repetitions.put(testMethod.getName(), repetitions.get(testMethod.getName()) + 1);

        times.putIfAbsent(testMethod.getName(), new Duration[REPETITIONS]);
        times.get(testMethod.getName())[repetitions.get(testMethod.getName()) - 1] = duration;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {

        PrintWriter stdDevFile = null;
        PrintWriter timesFile = null;
        String filesPath = "C:\\Users\\Francisco\\Documents\\phd\\Projects\\Golden\\docs\\plots\\";
        String filesType = ".csv";
        String fileTimesName = "times_v";
        String fileStdDevName = "stdDev_v";
        String testClass = extensionContext.getDisplayName();

        if (testClass.endsWith("2")) {
            stdDevFile = new PrintWriter(new File(filesPath + fileStdDevName + "2" + filesType));
            timesFile = new PrintWriter(new File(filesPath + fileTimesName + "2" + filesType));
        } else {
            stdDevFile = new PrintWriter(new File(filesPath + fileStdDevName + "1" + filesType));
            timesFile = new PrintWriter(new File(filesPath + fileTimesName + "1" + filesType));
        }

        // Calculate all the averages
        for (String entry : times.keySet()) {
            Duration average = calculateAverage(times.get(entry));
            System.out.println("-----------------------------");
            System.out.println(entry + ": " + average.toNanos());
            System.out.println(entry + ": " + new DecimalFormat("#0.000").format(average.toNanos() / 1000000.0) + " ms");
            timesFile.print(entry + "," + new DecimalFormat("#0.000").format(average.toNanos() / 1000000.0) + "\n");
//            stdDevFile.printf("%s,%10.5f\n", entry, getStandardDeviation(times.get(entry), getMean(times.get(entry))));
        }

        timesFile.close();
        stdDevFile.close();
    }

    /**
     * Calculate average excluding values away from mean more than 1 stdDev.
     *
     * @param durations
     * @return
     */
    private Duration calculateAverage(Duration[] durations) {
        Duration mean = getMean(durations);
        Duration stdDev = getStandardDeviation(durations, mean);
        Duration sum = Duration.ZERO;
        int cont = 0;
        for (int I = WARMUP_OFFSET; I < durations.length; I++) {
            if (durations[I].minus(mean).abs().compareTo(stdDev) <= 0) {
                cont++;
                sum = sum.plus(durations[I]);
            }
        }
        return sum.dividedBy(cont);
    }

    private ExtensionContext.Store getStore(ExtensionContext extensionContext) {
        return extensionContext.getStore(ExtensionContext.Namespace.create(getClass(), extensionContext));
    }
}
