package org.reactome.server.analysis.parser.util;

import java.time.Duration;

import static org.reactome.server.analysis.parser.util.ConstantHolder.WARMUP_OFFSET;

public class Statistics {

    public static Duration getMean(Duration[] values) {
        Duration total = Duration.ZERO;
        for (int I = WARMUP_OFFSET; I < values.length; I++) {
            total = total.plus(values[I]);
        }
        return total.dividedBy(values.length - WARMUP_OFFSET);
    }

    public static Duration getStandardDeviation(Duration[] values, Duration mean) {
        Duration sum = Duration.ZERO;
        for (int I = WARMUP_OFFSET; I < values.length; I++) {
            long nanos = (long) Math.pow(values[I].toNanos() - mean.toNanos(), 2);
            sum = sum.plus(Duration.ofNanos(nanos));
        }
        return Duration.ofNanos((long) Math.sqrt(sum.toNanos() / (values.length - WARMUP_OFFSET)));
    }
}
