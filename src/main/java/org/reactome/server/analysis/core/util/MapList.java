package org.reactome.server.analysis.core.util;

import java.io.Serializable;
import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Luis Sánchez <luis.sanchez@uib.no>
 */
public class MapList<S, T> implements Serializable {

    private String keyLabel;
    private String valueLabel;

    protected Map<S, List<T>> map = new HashMap<>();

    public MapList() {
        this.keyLabel = "";
        this.valueLabel = "";
    }

    public MapList(String keyLabel, String valueLabel) {
        this.keyLabel = keyLabel;
        this.valueLabel = valueLabel;
    }

    public String getKeyLabel() {
        return keyLabel;
    }

    public void setKeyLabel(String keyLabel) {
        this.keyLabel = keyLabel;
    }

    public String getValueLabel() {
        return valueLabel;
    }

    public void setValueLabel(String valueLabel) {
        this.valueLabel = valueLabel;
    }

    public boolean add(S identifier, T elem) {
        List<T> aux = getOrCreate(identifier);
        return aux.add(elem);
    }

    public boolean add(S identifier, Set<T> set) {
        List<T> aux = getOrCreate(identifier);
        return aux.addAll(set);
    }

    public boolean add(S identifier, List<T> list) {
        List<T> aux = getOrCreate(identifier);
        return aux.addAll(list);
    }

    public void addAll(MapList<S, T> map) {
        for (S s : map.keySet()) {
            this.add(s, map.getElements(s));
        }
    }

    public List<T> getElements(S identifier) {
        return map.get(identifier);
    }

    /**
     * Get the value list using a key, or create an empty value list for the key.
     *
     * @param identifier
     * @return
     */
    private List<T> getOrCreate(S identifier) {
        List<T> list = map.get(identifier);
        if (list == null) {
            list = new ArrayList<T>();
            map.put(identifier, list);
        }
        return list;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }


    public Set<S> keySet() {
        return map.keySet();
    }

    /**
     * Removes a key from the Map with its corresponding list.
     *
     * @param key
     * @return
     */
    public List<T> remove(S key) {
        return this.map.remove(key);
    }

    /**
     * Gathers all the elements of all the value lists of the map.
     *
     * @return
     */
    public List<T> values() {
        List<T> rtn = new ArrayList<>();
        for (List<T> ts : map.values()) {
            rtn.addAll(ts);
        }
        return rtn;
    }

    /**
     * Return the number of entry keys in the map.
     *
     * @return
     */
    public int size() {
        return map.size();
    }

    public int expandedSize() {
        int total = 0;
        for (List<T> ts : map.values()) {
            total += ts.size();
        }
        return total;
    }

    public Set<Map.Entry<S, List<T>>> entrySetInternal() {
        return map.entrySet();
    }

    public boolean containsKey(S key) {
        return map.containsKey(key);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (S key : map.keySet()) {
            for (T value : map.get(key)) {
                result.append(",{" + valueLabel + ":" + value + "," + keyLabel + ":\"" + key + "\"}");
            }
        }
        return "[" + (result.toString().length() > 0 ? result.toString().substring(1) : "") + "]";
    }
}
