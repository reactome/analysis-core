package org.reactome.server.analysis.core.model;

import org.reactome.server.analysis.core.util.OnthologyHttpClient;

import java.util.*;

/**
 * @author Luis Francisco Hernández Sánchez
 */

public class OnthologyGraph {
    /**
     * The term "00000" which represents a generic protein modification
     */
    Term root;

    /**
     * Map containing all the term nodes in the graph. It maps from a string id such as "00046" to the {@link Term}
     * object which contains the connections to the parent and children nodes.
     */
    HashMap<String, Term> terms;

    public Term getRoot() {
        return root;
    }

    public void setRoot(Term root) {
        this.root = root;
    }

    /**
     * Initializes the graph with only one term, which is the root node "00000"
     */
    public OnthologyGraph() {
        root = new Term("00000");
        this.terms = new HashMap<>();
        terms.put(root.getId(), root);
    }

    /**
     * Adds a term and all its hierarchical ancestors up until the root node
     *
     * @param id The PSIMOD id for the term. Ex. "00046"
     */
    public void addTerm(String id) {     //
        if (terms.containsKey(id)) {
            return;
        }
        Term term = new Term(id);
        terms.put(id, term);        //At this point the Graph contains at least the root and this term

        for (Term parent : OnthologyHttpClient.getRelatedTerms(term.getId(), "parents")) {
            term.parents.add(parent);
            this.addTerm(parent.getId());
            terms.get(parent.getId()).children.add(term);
        }
    }

    /**
     * Gathers all ancestors from the direct parents to the root node.
     *
     * @param id The PSIMOD id for the term. Ex. "00046"
     * @return The set of ancestors without a specific order
     */
    public Set<String> getAncestorsOf(String id) {
        Set<String> result = new HashSet<>();
        for (Term parent : this.terms.get(id).parents) {
            result.add(parent.getId());
            result.addAll(getAncestorsOf(parent.getId()));
        }
        return result;
    }

    /**
     * Fill the graph completely starting from the root node "00000"
     */
    public void fillChildren() {
        Queue<Term> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Term visitedTerm = queue.poll();
            for (Term childTerm : OnthologyHttpClient.getRelatedTerms(visitedTerm.getId(), "children")) {
                if (!terms.containsKey(childTerm.getId())) {    //If term has not been added to the graph, then queue to find its children later
                    queue.add(childTerm);
                    terms.put(childTerm.getId(), childTerm);
                    visitedTerm.addChild(childTerm);
                } else {
                    visitedTerm.addChild(terms.get(childTerm.getId()));
                }
            }
        }
    }
}
