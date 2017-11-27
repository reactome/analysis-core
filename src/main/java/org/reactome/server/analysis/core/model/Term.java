package org.reactome.server.analysis.core.model;

import org.reactome.server.analysis.core.util.OnthologyHttpClient;

import java.util.Set;
import java.util.TreeSet;

/**
 * A term of the PSIMOD onthology (http://www.ebi.ac.uk/ols/ontologies/mod)
 * It is also a node of the {@link OnthologyGraph}
 *
 * @author Luis Francisco Hernández Sánchez
 */

public class Term implements Comparable {

    /**
     * The PSIMOD id
     */
    private String id;
    private String iri;
    private String label;
    private String description;
    private String short_form;
    private String obo_id;

    public Set<Term> parents;
    public Set<Term> children;

    public Term(String iri, String label, String description, String short_form, String obo_id) {
        this.iri = iri;
        this.label = label;
        this.description = description;
        this.short_form = short_form;
        this.obo_id = obo_id;
    }

    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShort_form() {
        return short_form;
    }

    public void setShort_form(String short_form) {
        this.short_form = short_form;
    }

    public String getObo_id() {
        return obo_id;
    }

    public void setObo_id(String obo_id) {
        this.obo_id = obo_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Term(String id) {
        this.id = id;
        children = new TreeSet<>();
        parents = new TreeSet<>();
    }

    @Override
    public String toString() {
        return this.id;
    }

    public void fillChildren() {
        this.children.addAll(OnthologyHttpClient.getRelatedTerms(this.id, "children"));
        for (Term child : children) {
            child.fillChildren();
        }
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Term) && (((Term) o).getId()).equals(this.getId());
    }

    public void addChild(Term childTerm) {
        this.children.add(childTerm);
        childTerm.parents.add(this);
    }


    @Override
    public int compareTo(Object o) {
        return this.getId().compareTo(((Term) o).getId());
    }
}
