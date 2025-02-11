package org.reactome.server.analysis.core.model;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharSequenceNodeFactory;
import org.reactome.server.analysis.core.model.resource.Resource;
import org.reactome.server.analysis.core.util.MapSet;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class IdentifiersMap<T> implements Serializable {

    private RadixTree<MapSet<Resource, T>> tree;

    public IdentifiersMap() {
        NodeFactory nodeFactory = new DefaultCharSequenceNodeFactory();
        this.tree = new ConcurrentRadixTree<>(nodeFactory);
    }

    private MapSet<Resource, T> getOrCreateResourceEntitiesMap(String identifier) {
        MapSet<Resource, T> map = this.tree.getValueForExactKey(identifier);
        if (map == null) {
            map = new MapSet<>();
            this.tree.put(identifier, map);
        }
        return map;
    }

    public boolean add(String identifier, Resource resource, T node) {
        if (identifier == null || resource == null || node == null) return false;
        String id = identifier.trim().toUpperCase();
        MapSet<Resource, T> map = getOrCreateResourceEntitiesMap(id);
        return map.add(resource, node);
    }

    public MapSet<Resource, T> get(AnalysisIdentifier identifier) {
        Set<AnalysisIdentifier> identifiers = expandIdentifierWithPolimorfism(identifier);

        MapSet<Resource, T> rtn = new MapSet<>();
        for (AnalysisIdentifier aux : identifiers) {
            String id = aux.getId().toUpperCase();
            MapSet<Resource, T> res = this.tree.getValueForExactKey(id);
            if (res != null) {
                rtn.addAll(res);
            }
        }
        return rtn;
    }

    public MapSet<Resource, T> get(String identifier) {
        MapSet<Resource, T> res = this.tree.getValueForExactKey(identifier.toUpperCase());
        if (res != null) return res;
        return new MapSet<>();
    }

    /**
     * Returns all the identifiers in the Map (upper Case)
     *
     * @return all the identifiers in the Map (upper Case)
     */
    public Set<String> keySet() {
        Set<String> keySet = new HashSet<>();
        for (CharSequence charSequence : this.tree.getKeysStartingWith("")) {
            keySet.add(String.valueOf(charSequence));
        }
        return keySet;
    }

    public Set<T> values() {
        Set<T> rtn = new HashSet<>();
        for (String s : keySet()) {
            rtn.addAll(get(s).values());
        }
        return rtn;
    }

    private Set<AnalysisIdentifier> expandIdentifierWithPolimorfism(AnalysisIdentifier identifier) {
        //The compiler takes care of replacing this where the variables are used, so no worries for performance
        String UNIPROT = "[OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2}";
//        String UNIPROT_POLIMORFISM = UNIPROT + "\\-\\d+$";

        Set<AnalysisIdentifier> rtn = new HashSet<>();
        rtn.add(identifier);
        String id = identifier.getId().toUpperCase();
        if (id.matches(UNIPROT) && !id.contains("-")) {
            for (CharSequence sequence : this.tree.getKeysStartingWith(id + "-")) {
//                String aux = sequence.toString();
//                if(aux.matches(UNIPROT_POLIMORFISM)){
                rtn.add(new AnalysisIdentifier(sequence.toString(), identifier.getExp()));
//                }
            }
        }
        return rtn;
    }
}
