package org.reactome.server.analysis.core.importer.psimod;

import org.reactome.server.analysis.core.importer.psimod.PsiModRetrieval.Relations;

import java.util.HashMap;

/**
 * @author Luis Francisco Hernández Sánchez
 */
@SuppressWarnings("unused")
public class PsiModGraph {
    /**
     * The term "00000" which represents a generic protein modification
     */
    private Term root;

    /**
     * Map containing all the term nodes in the graph. It maps from a string id such as "00046" to the {@link Term}
     * object which contains the connections to the parent and children nodes.
     */
    private HashMap<String, Term> terms;

    /**
     * Initializes the graph with only one term, which is the root node "00000"
     */
    public PsiModGraph() {
        root = new Term("00000");
        this.terms = new HashMap<>();
        terms.put(root.getId(), root);
    }

    /**
     * Adds a term and all its hierarchical ancestors up until the root node
     *
     * @param identifier The PSIMOD identifier(s) for the term. Ex. "00046"
     */
    public void addTerm(String... identifier) {
        for (String id : identifier) {
            if (!terms.containsKey(id)) {
                final Term term = new Term(id);
                terms.put(id, term);

                for (Term aux : PsiModRetrieval.getRelatedTerms(term.getId(), Relations.parents)) {
                    addTerm(aux.getId());
                    Term parent = terms.get(aux.getId());
                    term.addParent(parent);
                }
            }
        }
    }

    public Term getRoot() {
        return root;
    }

    public Term getTerm(String id) {
        return terms.get(id);
    }
}
