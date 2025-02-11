package org.reactome.server.analysis.core.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.reactome.server.analysis.core.model.*;
import org.reactome.server.analysis.core.model.identifier.Identifier;
import org.reactome.server.analysis.core.model.identifier.MainIdentifier;
import org.reactome.server.analysis.core.model.resource.MainResource;
import org.reactome.server.analysis.core.model.resource.Resource;
import org.reactome.server.analysis.core.model.resource.ResourceFactory;
import org.reactome.server.analysis.core.result.exception.DataFormatException;
import org.reactome.server.analysis.core.result.external.ExternalAnalysisResult;
import org.reactome.server.analysis.core.result.external.ExternalPathwayNodeSummary;
import org.reactome.server.analysis.core.result.model.*;
import org.reactome.server.analysis.core.util.MapSet;
import org.reactome.server.graph.domain.model.Species;

import java.util.*;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class AnalysisStoredResult {
    private static final Integer PAGE_SIZE = 20;

    private AnalysisSummary summary;
    private List<PathwayNodeSummary> pathways;
    private Set<AnalysisIdentifier> notFound;
    private ExpressionSummary expressionSummary;
    private List<ResourceSummary> resourceSummary = new ArrayList<>();
    private List<SpeciesSummary> speciesSummary = new ArrayList<>();
    private List<String> warnings;

    public AnalysisStoredResult(UserData userData, HierarchiesData data) {
        this.warnings = userData.getWarningMessages();
        this.notFound = data.getNotFound();
        this.pathways = new LinkedList<>();
        this.expressionSummary = new ExpressionSummary(userData);
    }

    public AnalysisStoredResult(String token, ExternalAnalysisResult result) {
        this.summary = new AnalysisSummary(token, result.getSummary());
        this.expressionSummary = new ExpressionSummary(result.getExpressionSummary());
        this.pathways = new ArrayList<>();
        this.warnings = result.getWarnings();

        if (result.getNotFound() != null) {
            this.notFound = new HashSet<>();
            result.getNotFound().forEach(nf -> this.notFound.add(new AnalysisIdentifier(nf)));
        }

        Map<String, Integer> resourceHits = new HashMap<>();
        Map<SpeciesNode, Integer> speciesHits = new HashMap<>();
        int total = 0;
        if (result.getPathways() != null) {
            total = result.getPathways().size();
            for (ExternalPathwayNodeSummary epns : result.getPathways()) {
                PathwayNodeSummary pns = new PathwayNodeSummary(epns);
                for (String mr : epns.getData().getResources()) {
                    int n = resourceHits.getOrDefault(mr, 0);
                    resourceHits.put(mr, n + 1);
                }

                int n = speciesHits.getOrDefault(pns.getSpecies(), 0);
                speciesHits.put(pns.getSpecies(), n + 1);
                this.pathways.add(pns);
            }
        }

        setResourceSummary(resourceHits, total);
        setSpeciesSummary(speciesHits);
    }

    public void setHitPathways(List<PathwayNode> pathwayNodes) {
        //At the time we set the hit pathways, we also initialize resource summary
        Map<String, Integer> resourceHits = new HashMap<>();
        Map<SpeciesNode, Integer> speciesHits = new HashMap<>();
        Integer total = 0;
        for (PathwayNode pathwayNode : pathwayNodes) {
            PathwayNodeData data = pathwayNode.getPathwayNodeData();
            if (data.getEntitiesPValue() == null) continue;

            total++;
            for (MainResource mr : pathwayNode.getPathwayNodeData().getResources()) {
                if (data.getEntitiesPValue(mr) != null) {
                    int n = resourceHits.getOrDefault(mr.getName(), 0);
                    resourceHits.put(mr.getName(), n + 1);
                }
            }
            int n = speciesHits.getOrDefault(pathwayNode.getSpecies(), 0);
            speciesHits.put(pathwayNode.getSpecies(), n + 1);
            this.pathways.add(new PathwayNodeSummary(pathwayNode));
        }
        setResourceSummary(resourceHits, total);
        setSpeciesSummary(speciesHits);
    }

    private void setResourceSummary(Map<String, Integer> resourceHits, Integer total) {
        for (String resource : resourceHits.keySet()) {
            resourceSummary.add(new ResourceSummary(resource, resourceHits.get(resource)));
        }
        resourceSummary.sort(Collections.reverseOrder());
        //Total is always inserted on top
        resourceSummary.add(0, new ResourceSummary("TOTAL", total));
    }

    private void setSpeciesSummary(Map<SpeciesNode, Integer> speciesHits) {
        for (SpeciesNode speciesNode : speciesHits.keySet()) {
            speciesSummary.add(new SpeciesSummary(speciesNode, speciesHits.get(speciesNode)));
        }
        speciesSummary.sort(Collections.reverseOrder());
    }

    public void setSummary(AnalysisSummary summary) {
        this.summary = summary;
    }

    public ExpressionSummary getExpressionSummary() {
        return expressionSummary;
    }

    public FoundElements getFoundElmentsForPathway(String pathway, String resource) {
        PathwayNodeSummary aux = getPathway(pathway);
        if (aux != null) {
            List<String> columnNames = getExpressionSummary().getColumnNames();
            FoundEntities identifiers = (new FoundEntities(aux, columnNames)).filter(resource);
            FoundInteractors interactors = (new FoundInteractors(aux, columnNames)).filter(resource);
            if (identifiers != null) {
                return new FoundElements(pathway, identifiers, interactors, columnNames);
            }
        }
        return null;
    }

    public List<FoundElements> getFoundElmentsForPathways(List<String> pathways, String resource) {
        List<FoundElements> rtn = new ArrayList<>();
        for (String pathway : pathways) {
            PathwayNodeSummary aux = getPathway(pathway);
            if (aux != null) {
                List<String> columnNames = getExpressionSummary().getColumnNames();
                FoundEntities identifiers = (new FoundEntities(aux, columnNames)).filter(resource);
                FoundInteractors interactors = (new FoundInteractors(aux, columnNames)).filter(resource);
                if (identifiers != null) {
                    rtn.add(new FoundElements(pathway, identifiers, interactors, columnNames));
                }
            }
        }
        return rtn;
    }

    public FoundEntities getFoundEntities(String pathway, String resource) {
        FoundEntities pi = null;
        PathwayNodeSummary aux = getPathway(pathway);
        if (aux != null) {
            List<String> columnNames = getExpressionSummary().getColumnNames();
            pi = (new FoundEntities(aux, columnNames)).filter(resource);
        }
        return pi;
    }

    public FoundInteractors getFoundInteractors(String pathway) {
        FoundInteractors pi = null;
        PathwayNodeSummary aux = getPathway(pathway);
        if (aux != null) {
            List<String> columnNames = getExpressionSummary().getColumnNames();
            pi = new FoundInteractors(aux, columnNames);
        }
        return pi;
    }

    public Set<AnalysisIdentifier> getAnalysisIdentifiers() {
        Set<AnalysisIdentifier> rtn = new HashSet<>();
        for (Identifier identifier : getFoundEntitiesMap().keySet()) {
            rtn.add(identifier.getValue());
        }
        return rtn;
    }

    @JsonIgnore
    public MapSet<Identifier, MainIdentifier> getFoundEntitiesMap() {
        MapSet<Identifier, MainIdentifier> rtn = new MapSet<Identifier, MainIdentifier>();
        for (PathwayNodeSummary pathway : this.pathways) {
            rtn.addAll(pathway.getData().getIdentifierMap());
        }
        return rtn;
    }

    public MapSet<Identifier, MainIdentifier> getFoundEntitiesMap(MainResource mainResource) {
        MapSet<Identifier, MainIdentifier> rtn = new MapSet<>();

        MapSet<Identifier, MainIdentifier> aux = getFoundEntitiesMap();
        for (Identifier identifier : aux.keySet()) {
            for (MainIdentifier mainIdentifier : aux.getElements(identifier)) {
                if (mainIdentifier.getResource().equals(mainResource)) {
                    rtn.add(identifier, mainIdentifier);
                }
            }
        }
        return rtn;
    }

    public Set<Long> getFoundReactions(String pathwayId, String resource, boolean importableOnly) {
        return this.getFoundReactions(List.of(pathwayId), resource, importableOnly);
    }

    public Set<Long> getFoundReactions(List<String> pathwayIds, String resource, boolean importableOnly) {
        Set<Long> rtn = new HashSet<>();
        if (resource.toUpperCase().equals("TOTAL")) {
            for (PathwayNodeSummary pathway : this.pathways) {
                if (pathway.in(pathwayIds)) {
                    for (AnalysisReaction reaction : pathway.getData().getReactions(importableOnly)) {
                        rtn.add(reaction.getDbId());
                    }
                }
            }
        } else {
            Resource r = ResourceFactory.getResource(resource);
            if (r instanceof MainResource) {
                MainResource mainResource = (MainResource) r;
                for (PathwayNodeSummary pathway : this.pathways) {
                    if (pathway.in(pathwayIds)) {
                        for (AnalysisReaction reaction : pathway.getData().getReactions(mainResource)) {
                            rtn.add(reaction.getDbId());
                        }
                    }
                }
            }
        }
        return rtn;
    }

//    public Set<Long> getFoundReactions(String pathwayId, String resource, boolean importableOnly) {
//        return this.getFoundReactions(List.of(pathwayId), resource, importableOnly);
//    }

//    public Set<Long> getFoundReactions(List<String> pathwayIds, String resource, boolean importableOnly) {
//        Set<Long> rtn = new HashSet<>();
//        if (resource.toUpperCase().equals("TOTAL")) {
//            for (PathwayNodeSummary pathway : this.pathways) {
//                if (pathway.in(pathwayIds)) {
//                    for (AnalysisReaction reaction : pathway.getData().getReactions(importableOnly)) {
//                        rtn.add(reaction.getDbId());
//                    }
//                }
//            }
//        } else {
//            Resource r = ResourceFactory.getResource(resource);
//            if (r instanceof MainResource) {
//                MainResource mainResource = (MainResource) r;
//                for (PathwayNodeSummary pathway : this.pathways) {
//                    if (pathway.in(pathwayIds)) {
//                        for (AnalysisReaction reaction : pathway.getData().getReactions(mainResource)) {
//                            rtn.add(reaction.getDbId());
//                        }
//                    }
//                }
//            }
//        }
//        return rtn;
//    }

    public Set<AnalysisIdentifier> getNotFound() {
        return notFound;
    }

    public List<IdentifierSummary> getNotFoundIdentifiers() {
        List<IdentifierSummary> notFound = new LinkedList<>();
        for (AnalysisIdentifier identifier : getNotFound()) {
            notFound.add(new IdentifierSummary(identifier));
        }
        return notFound;
    }

    public PathwayNodeSummary getPathway(String identifier) {
        for (PathwayNodeSummary nodeSummary : this.pathways) {
            if (nodeSummary.is(identifier)) {
                return nodeSummary;
            }
        }
        return null;
    }

    public List<PathwayNodeSummary> getPathways() {
        return pathways;
    }

    public int getPage(String pathwayId, String sortBy, String order, String resource, Integer pageSize) {
        Collections.sort(this.pathways, getComparator(sortBy, order, resource));
        if (pageSize == null) pageSize = PAGE_SIZE;
        for (int i = 0; i < this.pathways.size(); i++) {
            PathwayNodeSummary pathway = this.pathways.get(i);
            if (pathway.is(pathwayId)) {
                return ((int) Math.floor(i / pageSize)) + 1;
            }
        }
        return -1;
    }

    public List<ResourceSummary> getResourceSummary() {
        return resourceSummary;
    }

    public List<SpeciesSummary> getSpeciesSummary() {
        return speciesSummary;
    }

    public AnalysisResult getResultSummary(String resource, boolean importableOnly) {
        return getResultSummary(null, "ASC", resource, null, null, importableOnly);
    }

    public AnalysisStoredResult filterPathways(String resource, boolean importableOnly) {
        return filterPathways(resource, null, true, null, null, importableOnly);
    }

    public AnalysisStoredResult filterPathways(String resource, Double pValue, boolean includeDisease, Integer min, Integer max, boolean importableOnly) {
        return filterPathways(null, resource, pValue, includeDisease, min, max, importableOnly);
    }

    public AnalysisStoredResult filterPathways(List<Species> species, String resource, Double pValue, boolean includeDisease, Integer min, Integer max, boolean importableOnly) {
        final boolean includeInteractors = summary.isInteractors();
        MainResource mr = ResourceFactory.getMainResource(resource);
        List<PathwayNodeSummary> pathways = new LinkedList<>();

        int resourceTotal = 0;
        Map<String, Integer> resourceHits = new HashMap<>();
        this.resourceSummary.stream()
                .filter(rs -> rs.getPathways() > 0 && !rs.getResource().equals("TOTAL"))
                .forEach(rs -> resourceHits.put(rs.getResource(), 0));
        Map<SpeciesNode, Integer> speciesHits = new HashMap<>();
        //if(species==null || species.isEmpty())
        this.getSpeciesSummary().forEach(s -> speciesHits.put(s.getSpeciesNode(), 0));

        for (PathwayNodeSummary pathway : this.pathways) {
            if (!includeDisease && pathway.isInDisease()) continue;
            PathwayNodeData data = pathway.getData();

            Double pwypValue = (mr == null) ? data.getEntitiesPValue(importableOnly) : data.getEntitiesPValue(mr);
            if (pwypValue == null) continue;

            boolean toAdd = pValue == null || pwypValue <= pValue;
            final int found = (mr == null) ?
                    data.getEntitiesFound(importableOnly) + data.getInteractorsFound(importableOnly) :
                    data.getEntitiesFound(mr) + data.getInteractorsFound(mr);
            toAdd &= found > 0;
            final int size = (mr == null) ?
                    data.getEntitiesCount(importableOnly) + (includeInteractors ? data.getInteractorsCount(importableOnly) : 0) :
                    data.getEntitiesCount(mr) + (includeInteractors ? data.getInteractorsCount(mr) : 0);
            toAdd &= (min == null || max == null) ? size > 0 : min <= size && max >= size;
            toAdd &= species == null || species.isEmpty() || pathway.getSpecies().isIn(species);
            if (toAdd) {
                pathways.add(pathway);
                resourceTotal += 1;
                for (MainResource r : data.getResources()) {
                    if ((!importableOnly || !r.isAuxMainResource()) && data.getEntitiesPValue(r) != null) {
                        resourceHits.put(r.getName(), resourceHits.getOrDefault(r.getName(), 0) + 1);
                    }
                }
                SpeciesNode speciesNode = pathway.getSpecies();
                speciesHits.put(speciesNode, 1 + speciesHits.getOrDefault(speciesNode, 0));
            }
        }
        this.pathways = pathways;
        setResourceSummaryFiltered(resourceHits, resourceTotal);
        setSpeciesSummaryFiltered(speciesHits);
        return this;
    }

    public AnalysisStoredResult filterPathwaysImportableOnly(boolean importableOnly) {
        List<PathwayNodeSummary> pathways = new LinkedList<>();

        int resourceTotal = 0;
        Map<String, Integer> resourceHits = new HashMap<>();
        this.resourceSummary.stream()
                .filter(rs -> rs.getPathways() > 0 && !rs.getResource().equals("TOTAL"))
                .forEach(rs -> resourceHits.put(rs.getResource(), 0));

        for (PathwayNodeSummary pathway : this.pathways) {
            PathwayNodeData data = pathway.getData();

            if (data.getResources().stream().allMatch(MainResource::isAuxMainResource)) continue;

            pathways.add(pathway);
            resourceTotal += 1;
            for (MainResource r : data.getResources()) {
                if (!r.isAuxMainResource() && data.getEntitiesPValue(r) != null) {
                    resourceHits.put(r.getName(), resourceHits.getOrDefault(r.getName(), 0) + 1);
                }
            }
        }
        this.pathways = pathways;
        setResourceSummaryFiltered(resourceHits, resourceTotal);
        return this;
    }

    private void setSpeciesSummaryFiltered(Map<SpeciesNode, Integer> speciesHits) {
        for (SpeciesSummary ss : speciesSummary) {
            ss.setFiltered(speciesHits.get(ss.getSpeciesNode()));
        }
    }

    private void setResourceSummaryFiltered(Map<String, Integer> resourceHits, Integer total) {
        resourceSummary.get(0).setFiltered(total);
        for (int i = 1; i < resourceSummary.size(); i++) {
            ResourceSummary rs = resourceSummary.get(i);
            rs.setFiltered(resourceHits.get(rs.getResource()));
        }
    }

    public AnalysisResult getResultSummary(String sortBy, String order, String resource, Integer pageSize, Integer page, boolean importableOnly) {
//        this.filterPathways(species, resource, pValue, includeDisease,  min, max);
        Collections.sort(this.pathways, getComparator(sortBy, order, resource));
        if (pageSize == null) pageSize = PAGE_SIZE;
        List<PathwaySummary> rtn = new LinkedList<>();
        if (page != null && page > 0) { // && this.pathways.size()>(pageSize*(page-1))){
            int end = (pageSize * page) > this.pathways.size() ? this.pathways.size() : (pageSize * page);
            for (int i = pageSize * (page - 1); i < end; ++i) {
                PathwayNodeSummary pathwayNodeSummary = this.pathways.get(i);
                rtn.add(new PathwaySummary(pathwayNodeSummary, resource.toUpperCase(), summary.isInteractors(), importableOnly));
            }
        } else {
            for (PathwayNodeSummary pathway : this.pathways) {
                rtn.add(new PathwaySummary(pathway, resource.toUpperCase(), summary.isInteractors(), importableOnly));
            }
        }
        return new AnalysisResult(this, rtn);
    }

    public AnalysisSummary getSummary() {
        return summary;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public List<PathwaySummary> filterByPathways(List<String> pathwayIds, String resource, boolean importableOnly) {
        this.filterPathways(resource, importableOnly);
        List<PathwaySummary> rtn = new LinkedList<>();
        for (PathwayNodeSummary pathway : this.pathways) {
            if (pathway.in(pathwayIds)) {
                rtn.add(new PathwaySummary(pathway, resource.toUpperCase(), summary.isInteractors(), importableOnly));
            }
        }
        return rtn;
    }

    public SpeciesFilteredResult filterBySpecies(Long speciesId, String resource, boolean importableOnly) {
        return filterBySpecies(speciesId, resource, null, "ASC", importableOnly);
    }

    public SpeciesFilteredResult filterBySpecies(Long speciesId, String resource, String sortBy, String order, boolean importableOnly) {
        if (resource != null) {
            Resource r = ResourceFactory.getResource(resource);

            this.filterPathways(resource, importableOnly);
            Collections.sort(this.pathways, getComparator(sortBy, order, resource));
            List<PathwayBase> rtn = new LinkedList<>();
            Double min = null, max = null;

            for (PathwayNodeSummary pathway : this.pathways) {
                if (pathway.getSpecies().getSpeciesID().equals(speciesId)) {
                    PathwaySummary aux = new PathwaySummary(pathway, resource.toUpperCase(), summary.isInteractors(), importableOnly);
                    rtn.add(new PathwayBase(aux));

                    List<Double> exps = new LinkedList<>();
                    if (r instanceof MainResource) {
                        exps = pathway.getData().getExpressionValuesAvg((MainResource) r);
                    } else if (resource.equals("TOTAL")) {
                        exps = pathway.getData().getExpressionValuesAvg();
                    }

                    for (Double exp : exps) {
                        if (min == null || exp < min) {
                            min = exp;
                        } else if (max == null || exp > max) {
                            max = exp;
                        }
                    }
                }
            }
            ExpressionSummary summary;
            if (this.expressionSummary == null) {
                summary = null;
            } else {
                summary = new ExpressionSummary(this.expressionSummary.getColumnNames(), min, max);
            }
            return new SpeciesFilteredResult(this.summary.getType(), summary, rtn);
        }
        throw new DataFormatException("Resource is null");
    }

    public List<Bin> getBinnedPathwaySize(int binSize, String resource, List<Species> speciesList, double pValue, boolean includeDisease) {
        final boolean includeInteractors = summary.isInteractors();
        final MainResource mr = ResourceFactory.getMainResource(resource);
        MapSet<Integer, Long> sizeMap = new MapSet<>();
        speciesList = speciesList == null ? new ArrayList<>() : speciesList;

        for (PathwayNodeSummary pathway : pathways) {
            if (!includeDisease && pathway.isInDisease()) continue;
            if (speciesList.isEmpty() || pathway.getSpecies().isIn(speciesList)) {
                final PathwayNodeData data = pathway.getData();
                final Double pv = (mr == null) ? data.getEntitiesPValue() : data.getEntitiesPValue(mr);
                if (pv != null && pv <= pValue) {
                    final int size = (mr == null) ?
                            data.getEntitiesCount() + (includeInteractors ? data.getInteractorsCount() : 0) :
                            data.getEntitiesCount(mr) + (includeInteractors ? data.getInteractorsCount(mr) : 0);
                    sizeMap.add(size / binSize, pathway.getPathwayId());
                }
            }
        }
        List<Bin> rtn = new ArrayList<>();
        for (Integer key : sizeMap.keySet()) {
            int value = sizeMap.getElements(key).size();
            rtn.add(new Bin(key, value));
        }
        return rtn;
    }

    private Comparator<PathwayNodeSummary> getComparator(String sortBy, String order, String resource) {
        AnalysisSortType sortType = AnalysisSortType.getSortType(sortBy);
        if (resource != null) {
            Resource r = ResourceFactory.getResource(resource);
            if (r instanceof MainResource) {
                MainResource mr = (MainResource) r;
                if (order != null && order.toUpperCase().equals("DESC")) {
                    return Collections.reverseOrder(ComparatorFactory.getComparator(sortType, mr));
                } else {
                    return ComparatorFactory.getComparator(sortType, mr);
                }
            }
        }
        if (order != null && order.toUpperCase().equals("DESC")) {
            return Collections.reverseOrder(ComparatorFactory.getComparator(sortType));
        } else {
            return ComparatorFactory.getComparator(sortType);
        }
    }
}
