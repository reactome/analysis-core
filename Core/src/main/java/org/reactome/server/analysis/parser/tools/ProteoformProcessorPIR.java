package org.reactome.server.analysis.parser.tools;

import org.apache.commons.lang.NotImplementedException;
import org.reactome.server.analysis.core.model.Proteoform;
import org.reactome.server.analysis.core.util.MapList;
import org.reactome.server.analysis.core.util.Pair;

public class ProteoformProcessorPIR {

    public static Proteoform getProteoform(String line, int i) {
        throw new NotImplementedException("Missing implementation for getProteoformPIR");
    }

    public static Proteoform getProteoform(String line) {
        return getProteoform(line, 0);
    }
}
