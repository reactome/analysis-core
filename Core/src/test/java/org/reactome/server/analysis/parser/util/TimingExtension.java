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

import static org.reactome.server.analysis.parser.InputPerformanceTest.WARMUP_OFFSET;
import static org.reactome.server.analysis.parser.util.ConstantHolder.*;
import static org.reactome.server.analysis.parser.util.Statistics.getMean;
import static org.reactome.server.analysis.parser.util.Statistics.getStandardDeviation;

public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback, AfterAllCallback {

    private static final Logger logger = Logger.getLogger(TimingExtension.class.getName());

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
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
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
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
