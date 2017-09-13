package org.reactome.server.analysis.core.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PsiModNode implements Comparable {

    private String identifier;
    private PsiModNode parent;
    private Set<PsiModNode> children;

    public PsiModNode(String identifier) {
        this.identifier = identifier;
        this.parent = null;
        this.children = new HashSet<>();
    }

    public PsiModNode addChild(PsiModNode child){
        PsiModNode rtn = null;
        for (PsiModNode aux : children) {
            if(aux.equals(child)){
                rtn = aux;
                break;
            }
        }

        if(rtn == null){
            children.add(child);
            child.setParent(this);
            rtn = child;
        }

        return rtn;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Map<String, PsiModNode> flatten() {
        Map<String, PsiModNode> flat = new HashMap<>();
        flat.put(identifier, this);
        for (PsiModNode child : children) {
            flat.putAll(child.flatten());
        }
        return flat;
    }

    private void setParent(PsiModNode parent) {
        if(this.parent!=null) throw new RuntimeException("You cannot add more than one parent per tree node");
        this.parent = parent;
    }

    public boolean isA(String identifier){
        return this.identifier.equals(identifier) || (parent != null && parent.isA(identifier));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PsiModNode that = (PsiModNode) o;

        return identifier != null ? identifier.equals(that.identifier) : that.identifier == null;
    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }

    @Override
    public int compareTo(Object o) {
        return this.identifier.compareTo(((PsiModNode) o).identifier);
    }
}
