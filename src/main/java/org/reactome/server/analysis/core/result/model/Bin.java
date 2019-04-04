package org.reactome.server.analysis.core.result.model;

public class Bin implements Comparable<Bin> {
    private Integer key;
    private Integer value;

    public Bin(Integer key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public int compareTo(Bin o) {
        return key.compareTo(o.key);
    }
}
