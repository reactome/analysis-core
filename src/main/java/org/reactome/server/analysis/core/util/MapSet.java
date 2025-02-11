package org.reactome.server.analysis.core.util;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class MapSet<S,T> implements Serializable {

    protected Map<S, Set<T>> map = new HashMap<>();

    public boolean add(S identifier, T elem){
        Set<T> aux = getOrCreate(identifier);
        return aux.add(elem);
    }

    public boolean add(S identifier, Set<T> set){
        Set<T> aux = getOrCreate(identifier);
        return aux.addAll(set);
    }

    public boolean add(S identifier, List<T> list){
        Set<T> aux = getOrCreate(identifier);
        return aux.addAll(list);
    }

    public void addAll(MapSet<S,T> map){
        for (S s : map.keySet()) {
            this.add(s, map.getElements(s));
        }
    }

    public Set<T> getElements(S identifier){
        return map.get(identifier);
    }

    private Set<T> getOrCreate(S identifier){
        return map.computeIfAbsent(identifier, k -> new HashSet<>());
    }

    public boolean isEmpty(){
        return map.isEmpty();
    }


    public Set<S> keySet(){
        return map.keySet();
    }

    public Set<T> remove(S key){
        return this.map.remove(key);
    }

    public Set<T> values() {
        Set<T> rtn = new HashSet<>();
        for (Set<T> ts : map.values()) {
            rtn.addAll(ts);
        }
        return rtn;
    }

    public int elementsCount() {
        return map.values().stream().mapToInt(Set::size).sum();
    }

    public Stream<Map.Entry<S, Set<T>>> stream() {
        return map.entrySet().stream();
    }
}
