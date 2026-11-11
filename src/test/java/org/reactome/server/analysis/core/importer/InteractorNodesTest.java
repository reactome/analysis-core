package org.reactome.server.analysis.core.importer;

import org.junit.Test;
import org.reactome.server.analysis.core.model.IdentifiersMap;
import org.reactome.server.analysis.core.model.InteractorNode;
import org.reactome.server.analysis.core.model.resource.Resource;
import org.reactome.server.analysis.core.model.resource.ResourceFactory;

import java.util.Set;

import static org.junit.Assert.*;

public class InteractorNodesTest {

    private final Resource uniprot = ResourceFactory.getResource("UniProt");

    @Test
    public void aPartnerWhoseIdentifierIsAnotherProteinsAliasIsItsOwnInteractor() {
        // Release 98: Q6NSH3 is a secondary identifier of P0DMU9, and an
        // interaction partner of its own in the Neurofascin interactions.
        IdentifiersMap<InteractorNode> map = new IdentifiersMap<>();
        InteractorNodes nodes = new InteractorNodes(map);
        InteractorNode p0dmu9 = nodes.getOrCreate(uniprot, "P0DMU9");
        nodes.addAlias("Q6NSH3", uniprot, p0dmu9);

        InteractorNode q6nsh3 = nodes.getOrCreate(uniprot, "Q6NSH3");

        assertNotSame(p0dmu9, q6nsh3);
        assertEquals(2, nodes.created());
        // An analysis submitting Q6NSH3 finds both: the protein, and the one it
        // is an alias of.
        Set<InteractorNode> found = map.get("Q6NSH3").getElements(uniprot);
        assertTrue(found.contains(p0dmu9));
        assertTrue(found.contains(q6nsh3));
    }

    @Test
    public void aPrimaryIdentifierFindsItsInteractorWhateverTheCase() {
        IdentifiersMap<InteractorNode> map = new IdentifiersMap<>();
        InteractorNodes nodes = new InteractorNodes(map);
        InteractorNode first = nodes.getOrCreate(uniprot, "P12345");

        assertSame(first, nodes.getOrCreate(uniprot, "p12345"));
        assertSame(first, nodes.getOrCreate(uniprot, " P12345 "));
        assertEquals(1, nodes.created());
    }

    @Test
    public void theSameIdentifierInAnotherResourceIsAnotherInteractor() {
        InteractorNodes nodes = new InteractorNodes(new IdentifiersMap<>());
        Resource ensembl = ResourceFactory.getResource("ENSEMBL");

        assertNotSame(nodes.getOrCreate(uniprot, "X1"), nodes.getOrCreate(ensembl, "X1"));
        assertEquals(2, nodes.created());
    }
}
