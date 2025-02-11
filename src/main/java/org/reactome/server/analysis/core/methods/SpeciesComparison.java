package org.reactome.server.analysis.core.methods;

import org.reactome.server.analysis.core.data.AnalysisData;
import org.reactome.server.analysis.core.exception.SpeciesNotFoundException;
import org.reactome.server.analysis.core.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@Component
public class SpeciesComparison {

    private static Logger logger = LoggerFactory.getLogger("methodsLogger");

    private final AnalysisData analysisData;
    private final EnrichmentAnalysis enrichmentAnalysis;

    @Autowired
    public SpeciesComparison(AnalysisData analysisData, EnrichmentAnalysis enrichmentAnalysis) {
        this.analysisData = analysisData;
        this.enrichmentAnalysis = enrichmentAnalysis;
    }

    public HierarchiesData speciesComparison(SpeciesNode speciesFrom, SpeciesNode speciesTo) throws SpeciesNotFoundException {
        UserData ud = getSyntheticUserData(speciesFrom);
        return enrichmentAnalysis.overRepresentation(ud.getIdentifiers(), speciesTo, false);
    }

    public UserData getSyntheticUserData(SpeciesNode species) throws SpeciesNotFoundException {
        EntitiesContainer graph = analysisData.getPhysicalEntityContainer();

        if(!analysisData.getPathwayHierarchies().keySet().contains(species)){
            throw new SpeciesNotFoundException(species.getSpeciesID() + " does not correspond to any of the current species");
        }

        Set<AnalysisIdentifier> speciesToIdentifiers = new HashSet<>();
        for (EntityNode node : graph.getAllNodes()) {
            if(species.equals(node.getSpecies())){
                if(node.getIdentifier()!=null){
                    speciesToIdentifiers.add(node.getIdentifier().getValue());
                }
            }
        }
        return new UserData(new LinkedList<>(), speciesToIdentifiers, null);
    }

    @Deprecated
    public UserData getSyntheticUserData(SpeciesNode speciesFrom, SpeciesNode speciesTo){
        EntitiesContainer graph = analysisData.getPhysicalEntityContainer();

        Set<AnalysisIdentifier> speciesToIdentifiers = new HashSet<>();
        for (EntityNode node : graph.getAllNodes()) {
            if(speciesFrom.equals(node.getSpecies())){
                EntityNode eq = node.getProjection(speciesTo);
                if(eq!=null && node.getIdentifier()!=null){
                    speciesToIdentifiers.add(node.getIdentifier().getValue());
                }
            }
        }
        return new UserData(new LinkedList<>(), speciesToIdentifiers, null);
    }
}
