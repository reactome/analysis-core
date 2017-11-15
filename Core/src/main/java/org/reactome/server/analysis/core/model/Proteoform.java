package org.reactome.server.analysis.core.model;

import org.reactome.server.analysis.core.util.MapList;
import org.reactome.server.analysis.parser.InputFormat_v3;
import org.reactome.server.analysis.parser.tools.ProteoformProcessorPRO;
import org.reactome.server.analysis.parser.tools.ProteoformProcessorSimple;
import org.reactome.server.analysis.parser.tools.ProteoformsProcessor;

import java.util.Collections;

public class Proteoform {
    private String UniProtAcc;
    private MapList<String, Long> PTMs;

    public Proteoform(String uniProtAcc) {
        UniProtAcc = uniProtAcc;
        PTMs = new MapList<>();
    }

    public Proteoform(String uniProtAcc, MapList<String, Long> PTMs) {
        UniProtAcc = uniProtAcc;
        this.PTMs = PTMs;
    }

    public String getUniProtAcc() {
        return UniProtAcc;
    }

    public void setUniProtAcc(String uniProtAcc) {
        UniProtAcc = uniProtAcc;
    }

    public MapList<String, Long> getPTMs() {
        return PTMs;
    }

    public void setPTMs(MapList<String, Long> PTMs) {
        this.PTMs = PTMs;
    }

    public String toString(InputFormat_v3.ProteoformFormat format) {
        StringBuilder str = new StringBuilder();
        String[] mods;
        switch (format) {
            case CUSTOM:
                return ProteoformProcessorSimple.getString(this);
            case GPMDB:
                //TODO
                break;
            case PRO:
                return ProteoformProcessorPRO.getString(this);
        }
        return str.toString();
    }


}
