package org.reactome.server.analysis.core.importer;

import org.reactome.server.analysis.core.model.IdentifiersMap;
import org.reactome.server.analysis.core.model.InteractorNode;
import org.reactome.server.analysis.core.model.resource.Resource;

import java.util.HashMap;
import java.util.Map;

/**
 * The interactor nodes of the interactors container: created once for each
 * primary identifier, and found in the container's map by that identifier and
 * by every alias recorded for them (gene names, secondary identifiers), so an
 * analysis can be submitted with any of them.
 *
 * An interaction partner is found by its primary identifier alone. Looking it
 * up in the container's map, aliases included, found another protein whenever
 * the partner's identifier was one of that protein's aliases, and the partner
 * was never created: which of the two survived depended on the order the
 * interactions were read in. Keys are trimmed and upper-cased, as
 * IdentifiersMap keys them, so a primary identifier finds what it found before.
 */
class InteractorNodes {

    private final IdentifiersMap<InteractorNode> interactorsMap;

    private final Map<Resource, Map<String, InteractorNode>> byPrimaryIdentifier = new HashMap<>();

    private int created = 0;

    InteractorNodes(IdentifiersMap<InteractorNode> interactorsMap) {
        this.interactorsMap = interactorsMap;
    }

    /** The interactor for this primary identifier in this resource, created if there is none yet. */
    InteractorNode getOrCreate(Resource resource, String identifier) {
        Map<String, InteractorNode> nodes = byPrimaryIdentifier.computeIfAbsent(resource, r -> new HashMap<>());
        String key = identifier.trim().toUpperCase();
        InteractorNode node = nodes.get(key);
        if (node == null) {
            node = new InteractorNode(identifier);
            nodes.put(key, node);
            interactorsMap.add(identifier, resource, node);
            created++;
        }
        return node;
    }

    /** Lets an analysis find this interactor by an alias as well; never used to find a partner. */
    void addAlias(String alias, Resource resource, InteractorNode node) {
        interactorsMap.add(alias, resource, node);
    }

    /** How many interactors have been created. */
    int created() {
        return created;
    }
}
