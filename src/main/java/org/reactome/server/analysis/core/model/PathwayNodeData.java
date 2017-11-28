package org.reactome.server.analysis.core.model;

import org.reactome.server.analysis.core.model.identifier.Identifier;
import org.reactome.server.analysis.core.model.identifier.MainIdentifier;
import org.reactome.server.analysis.core.model.resource.MainResource;
import org.reactome.server.analysis.core.util.MapSet;
import org.reactome.server.analysis.core.util.MathUtilities;

import java.util.*;

/**
 * For each pathway, this class contains the result of the analysis. There are three main parts in
 * this class: (1) the mapping between identifiers provided by the user and main identifiers used
 * for the result, (2) The set of reactions identifiers found for each main resource and (3) the
 * result counters -and statistics- for each main resource and for the combination of all of them
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class PathwayNodeData {
    //Please note that counter is used for each main identifier and for the combinedResult
    class Counter {
        Integer totalEntities = 0; //Pre-calculated in setCounters method
        Integer foundEntities = 0;
        Double entitiesRatio;
        Double entitiesPValue;
        Double entitiesFDR;

        Integer totalInteractors = 0; //Pre-calculated in setCounters method
        Integer foundInteractors = 0;
        Double interactorsRatio;

        //Aggregation
        Integer totalFound = 0;

        //TP-Based analysis
        Integer totalReactions = 0; //Pre-calculated in setCounters method
        Integer foundReactions = 0;
        Double reactionsRatio;
    }

    private MapSet<MainResource, String> foundTotal = new MapSet<>();

    /*
    The following structure plays two different roles
    1) While building the data structure it will keep track of the contained physical entities in the pathway
    2) During the analysis it will keep track of the seen elements
    */
    private MapSet<Identifier, MainIdentifier> entities = new MapSet<>();

    /*
    The following structure plays two different roles
    1) While building the data structure it will keep track of the contained reactions in the pathway
    2) During the analysis it will keep track of the seen reactions
    */
    private MapSet<MainResource, AnalysisReaction> reactions = new MapSet<>();

    //Analysis result containers
    private Map<MainResource, Counter> entitiesResult = new HashMap<>();
    private Counter combinedResult = new Counter();  //All main identifiers combined in one result

    public PathwayNodeData() {
    }

    public void addEntity(Identifier identifier, MainIdentifier mainIdentifier) {
        this.entities.add(identifier, mainIdentifier);
        this.foundTotal.add(mainIdentifier.getResource(), mainIdentifier.getValue().getId());
    }

    public void addReactions(MainResource mainResource, Set<AnalysisReaction> reactions) {
        this.reactions.add(mainResource, reactions);
    }


    public Integer getEntitiesAndInteractorsCount(){
        return combinedResult.totalFound;
    }

    public Integer getEntitiesAndInteractorsCount(MainResource resource){
        Counter counter = this.entitiesResult.get(resource);
        if (counter != null) {
            return counter.totalFound;
        }
        return 0;
    }

    public Integer getEntitiesAndInteractorsFound() {
        int total = 0;
        for (MainResource resource : foundTotal.keySet()) {
            total += foundTotal.getElements(resource).size();
        }
        return total;
    }

    public Integer getEntitiesAndInteractorsFound(MainResource resource) {
        Set<String> found = this.foundTotal.getElements(resource);
        return found == null ? 0 : found.size();
    }

    // ENTITIES Result

    public Set<AnalysisIdentifier> getFoundEntities() {
        Set<AnalysisIdentifier> rtn = new HashSet<>();
        for (Identifier identifier : entities.keySet()) {
            for (MainIdentifier mainIdentifier : this.entities.getElements(identifier)) {
                rtn.add(mainIdentifier.getValue());
            }
        }
        return rtn;
    }

    public Set<AnalysisIdentifier> getFoundEntities(MainResource resource) {
        Set<AnalysisIdentifier> rtn = new HashSet<>();
        for (Identifier identifier : entities.keySet()) {
            for (MainIdentifier mainIdentifier : this.entities.getElements(identifier)) {
                if (mainIdentifier.getResource().equals(resource)) {
                    rtn.add(mainIdentifier.getValue());
                }
            }
        }
        return rtn;
    }

    private List<Double> calculateAverage(List<List<Double>> expressionValues) {
        List<Double> avg = new LinkedList<>();
        int i = 0;
        for (List<Double> evs : expressionValues) {
            Double sum = 0.0;
            Double total = 0.0;
            for (Double ev : evs) {
                if (ev != null) {
                    sum += ev;
                    total++;
                }
            }
            if (total > 0.0) {
                avg.add(i++, sum / total);
            } else {
                avg.add(i++, null);
            }
        }
        return avg;
    }

    private List<AnalysisIdentifier> getEntitiesDuplication() {
        List<AnalysisIdentifier> rtn = new LinkedList<>();
        for (Identifier identifier : entities.keySet()) {
            for (MainIdentifier mainIdentifier : entities.getElements(identifier)) {
                rtn.add(mainIdentifier.getValue());
            }
        }
        return rtn;
    }

    private List<AnalysisIdentifier> getEntitiesDuplication(MainResource resource) {
        List<AnalysisIdentifier> rtn = new LinkedList<>();
        for (Identifier identifier : entities.keySet()) {
            for (MainIdentifier mainIdentifier : entities.getElements(identifier)) {
                if (mainIdentifier.getResource().equals(resource)) {
                    rtn.add(mainIdentifier.getValue());
                }
            }
        }
        return rtn;
    }

    public Integer getEntitiesCount() {
        return this.combinedResult.totalEntities;
    }

    public Integer getEntitiesCount(MainResource resource) {
        Counter counter = this.entitiesResult.get(resource);
        if (counter != null) {
            return counter.totalEntities;
        }
        return 0;
    }

    public Integer getEntitiesFound() {
        return getFoundEntities().size();
    }

    public Integer getEntitiesFound(MainResource resource) {
        return getFoundEntities(resource).size();
    }

    public Double getEntitiesPValue() {
        return this.combinedResult.entitiesPValue;
    }

    public Double getEntitiesPValue(MainResource resource) {
        Counter counter = this.entitiesResult.get(resource);
        if (counter != null) {
            return counter.entitiesPValue;
        }
        return null;
    }

    public Double getEntitiesFDR() {
        return this.combinedResult.entitiesFDR;
    }

    public Double getEntitiesFDR(MainResource resource) {
        Counter counter = this.entitiesResult.get(resource);
        if (counter != null) {
            return counter.entitiesFDR;
        }
        return null;
    }

    public Double getEntitiesRatio() {
        return this.combinedResult.entitiesRatio;
    }

    public Double getEntitiesRatio(MainResource resource) {
        Counter counter = this.entitiesResult.get(resource);
        if (counter != null) {
            return counter.entitiesRatio;
        }
        return null;
    }

    // REACTIONS Result

    public Set<AnalysisReaction> getReactions() {
        return reactions.values();
    }

    public Set<AnalysisReaction> getReactions(MainResource resource) {
        Set<AnalysisReaction> rtn = reactions.getElements(resource);
        if(rtn==null){
            rtn = new HashSet<AnalysisReaction>();
        }
        return rtn;
    }

    public Integer getReactionsCount() {
        return this.combinedResult.totalReactions;
    }

    public Integer getReactionsCount(MainResource mainResource) {
        Counter counter = this.entitiesResult.get(mainResource);
        if(counter!=null){
            return counter.totalReactions;
        }
        return 0;
    }

    public Integer getInteractorsReactionsCount() {
        return this.combinedResult.totalReactions;
    }

    public Integer getInteractorsReactionsCount(MainResource mainResource) {
        Counter counter = this.entitiesResult.get(mainResource);
        if(counter!=null){
            return counter.totalReactions;
        }
        return 0;
    }

    public Integer getReactionsFound(){
        return this.getReactions().size();
    }

    public Integer getReactionsFound(MainResource resource){
        return this.getReactions(resource).size();
    }

    public Double getReactionsRatio(){
        return this.combinedResult.reactionsRatio;
    }

    public Double getReactionsRatio(MainResource resource){
        Counter counter = this.entitiesResult.get(resource);
        if(counter !=null){
            return counter.reactionsRatio;
        }
        return null;
    }

    public Set<MainResource> getResources(){
        return this.entitiesResult.keySet();
    }

    public boolean hasResult(){
        return !foundTotal.isEmpty();
//        return !entities.isEmpty() || !interactors.isEmpty();
    }

    public void setEntitiesFDR(Double fdr){
        this.combinedResult.entitiesFDR = fdr;
    }

    public void setEntitiesFDR(MainResource resource, Double fdr){
        this.entitiesResult.get(resource).entitiesFDR = fdr;
    }

    //This is only called in build time
    protected void setCounters(PathwayNodeData speciesData){
        Set<AnalysisReaction> totalReactions = new HashSet<>();
        for (MainResource mainResource : reactions.keySet()) {
            Counter counter = getOrCreateCounter(mainResource);
            counter.totalReactions = reactions.getElements(mainResource).size();
            totalReactions.addAll(reactions.getElements(mainResource));
            counter.reactionsRatio = counter.totalReactions/speciesData.getReactionsCount(mainResource).doubleValue();
        }
        combinedResult.totalReactions += totalReactions.size(); totalReactions.clear();
        combinedResult.reactionsRatio =  combinedResult.totalReactions /speciesData.getReactionsCount().doubleValue();
        reactions = new MapSet<MainResource, AnalysisReaction>();

        MapSet<MainResource, AnalysisIdentifier> aux = new MapSet<>();
        for (Identifier identifier : entities.keySet()) {
            for (MainIdentifier mainIdentifier : entities.getElements(identifier)) {
                aux.add(mainIdentifier.getResource(), mainIdentifier.getValue());
            }
        }
        for (MainResource mainResource : aux.keySet()) {
            Counter counter = this.getOrCreateCounter(mainResource);
            counter.totalEntities = aux.getElements(mainResource).size();
            counter.entitiesRatio =  counter.totalEntities/speciesData.getEntitiesCount(mainResource).doubleValue();
            counter.totalFound = getEntitiesAndInteractorsFound(mainResource);
            combinedResult.totalEntities += counter.totalEntities;
        }
        combinedResult.entitiesRatio = this.combinedResult.totalEntities / speciesData.getEntitiesCount().doubleValue();
        combinedResult.totalFound = getEntitiesAndInteractorsFound();
        entities = new MapSet<>();
    }

    protected Double getScore(){
        return getScore(this.combinedResult);
    }

    protected Double getScore(MainResource mainResource){
        return getScore(this.entitiesResult.get(mainResource));
    }

    private Double getScore(Counter counter){
        Double entitiesPercentage = counter.foundEntities / counter.totalEntities.doubleValue();
        Double reactionsPercentage = counter.foundReactions / counter.totalReactions.doubleValue();
        return (0.75 * (reactionsPercentage)) + (0.25 * (entitiesPercentage));
    }

    private Counter getOrCreateCounter(MainResource mainResource){
        Counter counter = this.entitiesResult.get(mainResource);
        if (counter == null) {
            counter = new Counter();
            this.entitiesResult.put(mainResource, counter);
        }
        return counter;
    }
}