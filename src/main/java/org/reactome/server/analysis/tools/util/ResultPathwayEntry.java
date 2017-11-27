package org.reactome.server.analysis.tools.util;

import java.util.HashSet;
import java.util.Set;

public class ResultPathwayEntry {
    String pathway;
    private int entities_found;
    private int entities_total;         //This must match in both
    private int interactors_found;
    private int interactors_total;      //This must match in both
    private int reactions_found;
    private int reactions_total;

    private Set<String> submitted_entities_found;
    private Set<String> mapped_entities;
    private Set<String> submitted_entities_hit_interactor;
    private Set<String> interacts_with;
    private Set<String> found_reaction_identifiers;

    public ResultPathwayEntry() {
        this.pathway = "";
        this.entities_found = 0;
        this.entities_total = 0;
        this.interactors_found = 0;
        this.interactors_total = 0;
        this.reactions_found = 0;
        this.reactions_total = 0;
        this.submitted_entities_found = new HashSet<>();
        this.mapped_entities = new HashSet<>();
        this.submitted_entities_hit_interactor = new HashSet<>();
        this.interacts_with = new HashSet<>();
        this.found_reaction_identifiers = new HashSet<>();
    }

    public String getPathway() {
        return pathway;
    }

    public void setPathway(String pathway) {
        this.pathway = pathway;
    }

    public int getEntities_found() {
        return entities_found;
    }

    public void setEntities_found(int entities_found) {
        this.entities_found = entities_found;
    }

    public int getEntities_total() {
        return entities_total;
    }

    public void setEntities_total(int entities_total) {
        this.entities_total = entities_total;
    }

    public int getInteractors_found() {
        return interactors_found;
    }

    public void setInteractors_found(int interactors_found) {
        this.interactors_found = interactors_found;
    }

    public int getInteractors_total() {
        return interactors_total;
    }

    public void setInteractors_total(int interactors_total) {
        this.interactors_total = interactors_total;
    }

    public int getReactions_found() {
        return reactions_found;
    }

    public void setReactions_found(int reactions_found) {
        this.reactions_found = reactions_found;
    }

    public int getReactions_total() {
        return reactions_total;
    }

    public void setReactions_total(int reactions_total) {
        this.reactions_total = reactions_total;
    }

    public Set<String> getSubmitted_entities_found() {
        return submitted_entities_found;
    }

    public void setSubmitted_entities_found(String[] submitted_entities_found) {
        for (String id : submitted_entities_found) {
            this.submitted_entities_found.add(id);
        }
    }

    public Set<String> getMapped_entities() {
        return mapped_entities;
    }

    public void setMapped_entities(String[] mapped_entities) {
        for (String id : mapped_entities) {
            this.mapped_entities.add(id);
        }
    }

    public Set<String> getSubmitted_entities_hit_interactor() {
        return submitted_entities_hit_interactor;
    }

    public void setSubmitted_entities_hit_interactor(String[] submitted_entities_hit_interactor) {
        for (String id : submitted_entities_hit_interactor) {
            this.submitted_entities_hit_interactor.add(id);
        }

    }

    public Set<String> getInteracts_with() {
        return interacts_with;
    }

    public void setInteracts_with(String[] interacts_with) {
        for (String id : interacts_with) {
            this.interacts_with.add(id);
        }
    }

    public Set<String> getFound_reaction_identifiers() {
        return found_reaction_identifiers;
    }

    public void setFound_reaction_identifiers(String[] found_reaction_identifiers) {
        for (String id : found_reaction_identifiers) {
            this.found_reaction_identifiers.add(id);
        }
    }

    public void fillAttribute(int col, String value) {
        switch (col) {
            case 0:
                setPathway(value);
                break;
            case 2:
                setEntities_found(Integer.valueOf(value));
                break;
            case 3:
                setEntities_total(Integer.valueOf(value));
                break;
            case 4:
                setInteractors_found(Integer.valueOf(value));
                break;
            case 5:
                setInteractors_total(Integer.valueOf(value));
                break;
            case 9:
                setReactions_found(Integer.valueOf(value));
                break;
            case 10:
                setReactions_total(Integer.valueOf(value));
                break;
            case 14:
                setSubmitted_entities_found(value.split(";"));
                break;
            case 15:
                setMapped_entities(value.split(";"));
                break;
            case 16:
                setSubmitted_entities_hit_interactor(value.split(";"));
                break;
            case 17:
                setInteracts_with(value.split(";"));
                break;
            case 18:
                setFound_reaction_identifiers(value.split(";"));
                break;
        }
    }
}

