package org.reactome.server.analysis.core.model;

import org.reactome.server.analysis.core.model.identifier.MainIdentifier;
import org.reactome.server.analysis.core.util.MapSet;

import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class EntitiesContainer {

    private MapSet<MainIdentifier, EntityNode> nodes;

    public EntitiesContainer() {
        this.nodes = new MapSet<>();
    }

    public EntityNode add(EntityNode node) {
        Set<EntityNode> aux = nodes.getElements(node.getIdentifier());
        if (aux != null) {
            for (EntityNode entityNode : aux) {
                if (entityNode.equals(node)) return entityNode;
            }
        }
        this.nodes.add(node.getIdentifier(), node);
        return node;
    }

    public Set<EntityNode> getNodes(MainIdentifier mainIdentifier) {
        return nodes.getElements(mainIdentifier);
    }

    public Set<EntityNode> getAllNodes() {
        return nodes.values();
    }

    public void setOrthologiesCrossLinks(){
        for (EntityNode node : getAllNodes()) {
            node.setOrthologiesCrossLinks();
        }
    }

}
