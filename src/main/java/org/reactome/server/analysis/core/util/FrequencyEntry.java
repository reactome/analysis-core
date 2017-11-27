package org.reactome.server.analysis.core.util;

public class FrequencyEntry {
    private long reactions;
    private long pathways;
    private String identifier;
    private String type;

    public long getReactions() {
        return reactions;
    }

    public void setReactions(long reactions) {
        this.reactions = reactions;
    }

    public long getPathways() {
        return pathways;
    }

    public void setPathways(long pathways) {
        this.pathways = pathways;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FrequencyEntry(String identifier, String type, long reactions, long pathways) {
        this.reactions = reactions;
        this.pathways = pathways;
        this.identifier = identifier;
        this.type = type;
    }

    @Override
    public String toString() {
        return identifier + "," + type + "," + String.valueOf(pathways) + "," + String.valueOf(reactions);
    }

    public String toString(String separator) {
        return identifier + separator + type + separator + String.valueOf(pathways) + separator + String.valueOf(reactions);
    }

}
