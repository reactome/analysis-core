package org.reactome.server.analysis.core.result.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class FoundElements {

    private String pathway;
    private List<FoundEntity> entities;
    private List<FoundInteractor> interactors;
    private Set<String> resources;
    private List<String> expNames;
    private Integer foundEntities;
    private Integer foundInteractors;

    public FoundElements(String pathway, FoundEntities entities, FoundInteractors interactors, List<String> expNames) {
        this.pathway = pathway;
        this.resources = new HashSet<>();
        this.expNames = expNames;
        if(entities !=null){
            this.entities = entities.getIdentifiers();
            this.foundEntities = this.entities.size();
            this.resources.addAll(entities.getResources());
        }
        if(interactors!=null){
            this.interactors = interactors.getIdentifiers();
            this.foundInteractors = interactors.getFound();
            this.resources.addAll(interactors.getResources());
        }
    }

    public String getPathway() {
        return pathway;
    }

    public List<FoundEntity> getEntities() {
        return entities;
    }

    public List<FoundInteractor> getInteractors() {
        return interactors;
    }

    public Set<String> getResources() {
        return resources;
    }

    public List<String> getExpNames() {
        return expNames;
    }

    public Integer getFoundEntities() {
        return foundEntities;
    }

    public Integer getFoundInteractors() {
        return foundInteractors;
    }
}
