package org.reactome.server.analysis.parser.util;

import java.time.Duration;

public class Statistics {

    public static Duration getMean(Duration[] values) {
        Duration total = Duration.ZERO;
        for (int I = 0; I < values.length; I++) {
            total = total.plus(values[I]);
        }
        return total.dividedBy(values.length);
    }

    public static double getStandardDeviation(Duration[] values, Duration mean) {
        long sum = 0;
        for (int I = 0; I < values.length; I++) {
            sum += Math.pow(values[I].toNanos() - mean.toNanos(), 2);
        }
        return Math.sqrt(sum/values.length);
    }
}
