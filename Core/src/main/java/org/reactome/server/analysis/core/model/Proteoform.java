package org.reactome.server.analysis.core.model;

import org.reactome.server.analysis.core.util.MapList;
import org.reactome.server.analysis.parser.ParserExtended;
import org.reactome.server.analysis.parser.ParserProteoformPRO;
import org.reactome.server.analysis.parser.ParserProteoformSimple;

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

    public String toString(ParserExtended.ProteoformFormat format) {
        StringBuilder str = new StringBuilder();
        String[] mods;
        switch (format) {
            case SIMPLE:
                return ParserProteoformSimple.getString(this);
            case GPMDB:
                //TODO
                break;
            case PRO:
                return ParserProteoformPRO.getString(this);
        }
        return str.toString();
    }


}
