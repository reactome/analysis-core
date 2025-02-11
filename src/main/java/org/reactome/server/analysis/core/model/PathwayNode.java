package org.reactome.server.analysis.core.model;

import org.reactome.server.analysis.core.model.identifier.Identifier;
import org.reactome.server.analysis.core.model.identifier.InteractorIdentifier;
import org.reactome.server.analysis.core.model.identifier.MainIdentifier;
import org.reactome.server.analysis.core.model.resource.MainResource;
import org.reactome.server.graph.domain.model.Pathway;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class PathwayNode implements Serializable, Comparable<PathwayNode> {
    private String stId;
    private Long pathwayId;
    private String name;
    private boolean hasDiagram;
    private boolean isLowerLevelPathway = false;
    private boolean inDisease;

    private PathwayNode parent;
    private Set<PathwayNode> children;

    private PathwayNodeData data;

    public PathwayNode(String stId, Long pathwayId, String name, boolean hasDiagram, boolean inDisease) {
        this(null, stId, pathwayId, name, hasDiagram, inDisease);
    }

    protected PathwayNode(PathwayNode parent, String stID, Long pathwayId, String name, boolean hasDiagram, boolean inDisease) {
        this.parent = parent;
        this.stId = stID;
        this.pathwayId = pathwayId;
        this.name = name;
        this.hasDiagram = hasDiagram;
        this.inDisease = inDisease;
        this.children = new HashSet<>();
        this.data = new PathwayNodeData();
    }

    public PathwayNode addChild(Pathway p){
        PathwayNode node = new PathwayNode(this, p.getStId(), p.getDbId(), p.getDisplayName(), p.getHasDiagram(), p.getIsInDisease());
        this.children.add(node);
        return node;
    }

    public Set<PathwayNode> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }

    public PathwayNode getDiagram(){
        if(this.hasDiagram){
            return this;
        }
        if(parent != null){
            return parent.getDiagram();
        }
        return null;
    }

    public boolean isInDisease() {
        return inDisease;
    }

    protected Set<PathwayNode> getHitNodes(){
        Set<PathwayNode> rtn = new HashSet<>();
        if(this.data.hasResult()){
            rtn.add(this);
            for (PathwayNode node : children) {
                rtn.addAll(node.getHitNodes());
            }
        }
        return rtn;
    }

    public boolean isLowerLevelPathway() {
        return isLowerLevelPathway;
    }

    public Long getPathwayId() {
        return pathwayId;
    }

    public PathwayNode getParent() {
        return parent;
    }

    public PathwayNodeData getPathwayNodeData() {
        return data;
    }

    public SpeciesNode getSpecies(){
        if(this.parent==null){
            PathwayRoot root = (PathwayRoot) this;
            return root.getSpecies();
        }else{
            return parent.getSpecies();
        }
    }

    public String getStId() {
        return stId;
    }

    public void setLowerLevelPathway(boolean isLowerLevelPathway) {
        this.isLowerLevelPathway = isLowerLevelPathway;
    }

    protected void setCounters(PathwayNodeData speciesData){
        this.data.setCounters(speciesData);
        for (PathwayNode child : this.children) {
            child.setCounters(speciesData);
        }
    }

    public void setResultStatistics(Map<MainResource, Integer> sampleSizePerResource, Integer notFound, boolean includeInteractors){
        for (PathwayNode child : this.children) {
            child.setResultStatistics(sampleSizePerResource, notFound, includeInteractors);
        }
        this.data.setResultStatistics(sampleSizePerResource, notFound, includeInteractors);
    }

    public void process(MainIdentifier mainIdentifier, Set<AnalysisReaction> reactions){
        this.process(mainIdentifier, mainIdentifier, reactions);
    }

    public void process(Identifier identifier, MainIdentifier mainIdentifier, Set<AnalysisReaction> reactions){
        this.data.addEntity(identifier, mainIdentifier);
        this.data.addReactions(mainIdentifier.getResource(), reactions);
        if(this.parent!=null){
            this.parent.process(identifier, mainIdentifier, reactions);
        }
    }

    public void processInteractor(InteractorIdentifier identifier, MainIdentifier mainIdentifier, Set<AnalysisReaction> reactions){
        if(reactions!=null && !reactions.isEmpty()) {
            data.addInteractors(mainIdentifier, identifier);
            data.addReactions(mainIdentifier.getResource(), reactions);
            if (parent != null) {
                parent.processInteractor(identifier, mainIdentifier, reactions);
            }
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(PathwayNode o) {
        int rtn = this.data.getEntitiesPValue().compareTo(o.data.getEntitiesPValue());
        if(rtn==0){
            Double oScore = o.data.getScore(); Double thisScore = this.data.getScore();
            rtn = oScore.compareTo(thisScore);
            if(rtn==0){
                rtn = o.data.getEntitiesFound().compareTo(this.data.getEntitiesFound());
            }
        }
        return rtn;
    }
}
