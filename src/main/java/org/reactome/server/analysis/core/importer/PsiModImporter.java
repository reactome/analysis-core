package org.reactome.server.analysis.core.importer;

import org.reactome.server.analysis.core.model.PsiModNode;
import org.reactome.server.graph.domain.model.PsiMod;
import org.reactome.server.graph.service.SchemaService;
import org.reactome.server.graph.utils.ReactomeGraphCore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class PsiModImporter {

    public static PsiModNode getPsiModTree() {
        SchemaService service = ReactomeGraphCore.getService(SchemaService.class);

        PsiModNode rtn = new PsiModNode(null);

        Collection<PsiMod> psiMods = service.getByClass(PsiMod.class);
        for (PsiMod psiMod : psiMods) {
            PsiModNode psiModNode = rtn.flatten().get(psiMod.getIdentifier());
            if (psiModNode == null) {
                List<String> branch = getBranch(psiMod.getIdentifier());
                Collections.reverse(branch);
                PsiModNode aux = rtn;
                for (String s : branch) {
                    PsiModNode child = new PsiModNode(s);
                    aux = aux.addChild(child);
                }
            }
        }

        return rtn;
    }

    private static List<String> getBranch(String identifier){
        List<String> rtn = new ArrayList<>();
        rtn.add(identifier);
        //TODO: Query PSIMod to find out all the parents of psiMod and add them to the list
        return rtn;
    }

}
