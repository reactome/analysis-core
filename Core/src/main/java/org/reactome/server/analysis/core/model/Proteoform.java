package org.reactome.server.analysis.core.model;

import org.reactome.server.analysis.core.util.MapList;
import org.reactome.server.analysis.parser.ParserExtended;
import org.reactome.server.analysis.parser.ParserProteoformPRO;
import org.reactome.server.analysis.parser.ParserProteoformSimple;

public class Proteoform {
    private String UniProtAcc;          // The uniprot accession number including the optional isoform
    private Long startCoordinate;       // The start coordinate of the protein subsequence
    private Long endCoordinate;         // The end coordinate of the protein subsequence
    private MapList<String, Long> PTMs; // The list of post-translational modifications

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

    public Long getStartCoordinate() {
        return startCoordinate;
    }

    public void setStartCoordinate(Long startCoordinate) {
        this.startCoordinate = startCoordinate;
    }

    public Long getEndCoordinate() {
        return endCoordinate;
    }

    public void setEndCoordinate(Long endCoordinate) {
        this.endCoordinate = endCoordinate;
    }

    public MapList<String, Long> getPTMs() {
        return PTMs;
    }

    public void setPTMs(MapList<String, Long> PTMs) {
        this.PTMs = PTMs;
    }

    public String toString(ParserExtended.ProteoformFormat format) {
        switch (format) {
            case SIMPLE:
                return ParserProteoformSimple.getString(this);
            case PRO:
                return ParserProteoformPRO.getString(this);
            default:
                    return UniProtAcc + "," + startCoordinate + "-" + endCoordinate + "," + PTMs.toString();
        }
    }

    public void addPtm(String s, Long site) {
        PTMs.add(s, site);
    }
}
