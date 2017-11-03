package org.reactome.server.analysis.parser.util;

import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Method;
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

//        System.out.println(System.getProperty("user.dir"));
        PrintWriter stdDevFile = new PrintWriter(new File("target/test-classes/analysis/input/UniProt/stdDev.csv"));
        PrintWriter timesFile = new PrintWriter(new File("target/test-classes/analysis/input/UniProt/times.csv"));
        // Calculate all the averages
        for (String entry : times.keySet()) {
            Duration average = calculateAverage(times.get(entry));
            System.out.println(entry + ": " + average.toNanos());
            timesFile.printf("%s,%s\n", entry, average.toNanos());
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
        double stdDev = getStandardDeviation(durations, mean);
        Duration sum = Duration.ZERO;
        int cont = 0;
        for (Duration value : durations) {
            if (Math.abs(value.toNanos() - mean.toNanos()) <= stdDev) {
                cont++;
                sum = sum.plus(value);
            }
        }
        return sum.dividedBy(cont);
    }

    private ExtensionContext.Store getStore(ExtensionContext extensionContext) {
        return extensionContext.getStore(ExtensionContext.Namespace.create(getClass(), extensionContext));
    }
}
