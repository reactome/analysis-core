package org.reactome.server.analysis.core.importer;

import org.reactome.server.analysis.core.Main;
import org.reactome.server.analysis.core.importer.psimod.PsiModGraph;
import org.reactome.server.analysis.core.importer.psimod.Term;
import org.reactome.server.analysis.core.model.PsiModNode;
import org.reactome.server.graph.domain.model.PsiMod;
import org.reactome.server.graph.service.SchemaService;
import org.reactome.server.graph.utils.ReactomeGraphCore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class PsiModImporter {

    public static Map<String, PsiModNode> getPsiModMap() {

        Map<String, PsiModNode> psiModNodeMap = new HashMap<>();

        SchemaService service = ReactomeGraphCore.getService(SchemaService.class);

        PsiModGraph graph = getPsiModGraph(service.getByClass(PsiMod.class));

        for (Term term : graph.getRoot().getChildrenAll()) {
            psiModNodeMap.put(term.getId(), new PsiModNode(term.getId()));
        }

        for (Term termLeaf : graph.getRoot().getLeafs()) {
            PsiModNode nodeLeaf = psiModNodeMap.get(termLeaf.getId());
            fillParentsPath(termLeaf, nodeLeaf, psiModNodeMap);
        }

        return psiModNodeMap;
    }

    private static PsiModGraph getPsiModGraph(Collection<PsiMod> psiMods) {
        PsiModGraph psiModGraph = new PsiModGraph();
        String msgPrefix = "\rCreating the PSI-MOD ontology graph";
        int i = 0;
        int tot = psiMods.size();
        for (PsiMod psiMod : psiMods) {
            if (Main.VERBOSE) System.out.print(msgPrefix + " >> '" + psiMod.getDisplayName() + "' (" + (++i) + "/" + tot + ")");
            psiModGraph.addTerm(psiMod.getIdentifier());
        }
        if (Main.VERBOSE) System.out.println(msgPrefix + " >> Done.");
        return psiModGraph;
    }

    private static void fillParentsPath(Term term, PsiModNode node, Map<String, PsiModNode> psiModNodeMap) {
        if (!term.isRoot()) {
            for (Term termParent : term.getParents()) {
                PsiModNode nodeParent = psiModNodeMap.get(termParent.getId());
                node.addParent(nodeParent);
                fillParentsPath(termParent, nodeParent, psiModNodeMap);
            }
        }
    }

}
