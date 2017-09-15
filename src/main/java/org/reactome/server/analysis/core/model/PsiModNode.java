package org.reactome.server.analysis.core.model;

import java.util.HashSet;
import java.util.Set;

public class PsiModNode implements Comparable {

    private String identifier;
    private Set<PsiModNode> parents;

    public PsiModNode(String identifier) {
        this.identifier = identifier;
        this.parents = null;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void addParent(PsiModNode parent) {
        if (this.parents == null) this.parents = new HashSet<>();
        this.parents.add(parent);
    }

    public boolean isA(String identifier) {
        if (this.identifier.equals(identifier)) {
            return true;
        } else if (parents != null) {
            for (PsiModNode parent : parents) {
                if (parent.isA(identifier)) return true;
            }
        }
        return false;
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
