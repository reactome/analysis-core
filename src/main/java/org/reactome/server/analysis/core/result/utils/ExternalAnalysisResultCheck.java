package org.reactome.server.analysis.core.result.utils;

import org.apache.commons.lang3.StringUtils;
import org.reactome.server.analysis.core.data.AnalysisData;
import org.reactome.server.analysis.core.model.AnalysisType;
import org.reactome.server.analysis.core.model.resource.ResourceFactory;
import org.reactome.server.analysis.core.result.AnalysisStoredResult;
import org.reactome.server.analysis.core.result.external.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Aims to check inconsistencies in the provided {@link ExternalAnalysisResult} to avoid crashes when converted
 * to {@link AnalysisStoredResult}
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@Component
public class ExternalAnalysisResultCheck {

    private static final Pattern STID_PATTERN = Pattern.compile("R-[A-Z]{3}-[0-9]+(\\.[0-9]+)?");

    private AnalysisData analysisData;

    public ExternalAnalysisResultCheck() {
    }

    /**
     * Checks whether the {@link ExternalAnalysisResult} instance is valid.
     *
     * @return an empty list when true or a list of error messages otherwise
     */
    @SuppressWarnings("unused")
    public List<String> isValid(ExternalAnalysisResult result) {
        final List<String> messages = new ArrayList<>();
        checkSummary(result.getSummary(), messages);
        checkExpressionSummary(result.getExpressionSummary(), messages);
        checkPathways(result.getPathways(), result.getExpressionSummary(), messages);
        checkNotFound(result.getNotFound(), result.getExpressionSummary(), messages);
        return messages;
    }

    private void checkSummary(ExternalAnalysisSummary summary, final List<String> messages) {
        Integer reactomeVersion = analysisData.getDatabaseInfo().getVersion();
        Integer resultVersion = summary.getVersion();

        if (resultVersion == null) {
            messages.add("No version provided in the 'summary'");
        } else if (!reactomeVersion.equals(resultVersion)) {
            messages.add(String.format(
                    "The version of the provided result (%d) does not match with that of the analysis service (%d)",
                    reactomeVersion,
                    resultVersion));
        }

        if (summary.getSampleName() == null)
            messages.add("The summary does no contain the sample name");
        if (summary.getInteractors() == null)
            messages.add("The summary does no have indication of whether interactors where taken into account");
        if (summary.getProjection() == null)
            messages.add("The summary does no have indication of whether there was projection to human when the analysis was performed");

        AnalysisType type = summary.getType();
        if (type == null) messages.add("The provided analysis type is not supported");
    }

    private void checkExpressionSummary(ExternalExpressionSummary expressionSummary, final List<String> messages) {
        if (expressionSummary.getColumnNames() != null && !expressionSummary.getColumnNames().isEmpty()) {
            if (expressionSummary.getMin() == null)
                messages.add("No 'min' value provided in the expression summary");
            if (expressionSummary.getMax() == null)
                messages.add("No 'max' value provided in the expression summary");
            if (expressionSummary.getMin() != null && expressionSummary.getMax() != null) {
                if (expressionSummary.getMin() > expressionSummary.getMax())
                    messages.add("Wrong values for 'min' and 'max' in the expression summary (min is greater than max)");
            }
        }
    }

    private void checkPathways(List<ExternalPathwayNodeSummary> pathways,
                               ExternalExpressionSummary expressionSummary, final List<String> messages) {
        if (pathways == null) return;

        for (int i = 1; i <= pathways.size(); i++) {
            ExternalPathwayNodeSummary pathway = pathways.get(i - 1);
            if (pathway.getDbId() == null)
                messages.add("No database identifier 'dbId' provided for pathway " + i);
            if (pathway.getStId() == null) {
                messages.add("No stable identifier 'stId0 provided for pathway " + i);
            } else if (!STID_PATTERN.matcher(pathway.getStId()).matches()) {
                messages.add(String.format("'%s' stId provided for pathway %d is a wrong stable identifier", pathway.getStId(), i));
            }
            if (pathway.isInDisease() == null)
                messages.add("'isDisease' not specified for pathway " + i);
            if (pathway.isInDisease() == null)
                messages.add("'isDisease' not specified for pathway " + i);
            //llp not checked because it looks like it might get soon deprecated
            checkPathwayNodeData(pathway.getData(), expressionSummary, i, messages);
            checkSpecies(pathway.getSpecies(), i, messages);
        }
    }

    private void checkPathwayNodeData(ExternalPathwayNodeData data, ExternalExpressionSummary expressionSummary,
                                      final int i, final List<String> messages) {
        //Statistics check
        if (data.getStatistics() == null || data.getStatistics().isEmpty()) {
            messages.add(String.format("'statistics' not found for pathway %d", i));
        } else {
            final Set<String> resources = new HashSet<>();
            for (ExternalStatistics statistic : data.getStatistics()) {
                checkPathwayNodeStatistics(statistic, expressionSummary, i, messages);
                resources.add(statistic.getResource());
            }

            if (resources.size() < 2) {
                messages.add(String.format("'statistics' for pathway %d contains less than 2 entries", i));
            } else {
                if (!resources.contains("TOTAL")) {
                    messages.add(String.format("'statistics' for pathway %d is missing the resource 'TOTAL'", i));
                }
            }
        }


        boolean entities = data.getEntities() != null && !data.getEntities().isEmpty();
        boolean interactors = data.getInteractors() != null && !data.getInteractors().isEmpty();
        if (!entities && !interactors) {
            messages.add(String.format("No hit entities nor interactors reported for pathway %d", i));
        } else {
            if (entities) { //Entities check
                for (int j = 1; j <= data.getEntities().size(); j++) {
                    final ExternalIdentifier entity = data.getEntities().get(j - 1);
                    if (entity.getId() == null || entity.getId().isEmpty())
                        messages.add(String.format("No identifier provided for entity %d of pathway %d", j, i));

                    int expValues = expressionSummary.getExpValues();
                    List<Double> exp = entity.getExp();
                    if (expValues == 0) {
                        if (exp != null && exp.size() > 0) {
                            messages.add(String.format("'exp' field must not contain any value in the entity %d for pathway %d", j, i));
                        }
                    } else {
                        int aux = (exp == null) ? 0 : exp.size();
                        if (aux != expValues) {
                            messages.add(String.format("'exp' field contains %d values but it must contain %d" + 
                                                       " values in the entity %d for pathway %d", aux, expValues, j, i));
                        } else {
                            for (int k = 1; j <= exp.size(); j++) {
                                final Double value = exp.get(k - 1);
                                if (value == null) {
                                    messages.add(String.format("The value in position %d in 'exp' field is null in entity %d for pathway %d",
                                            k, j, i));
                                } else if (!expressionSummary.isValid(value)) {
                                    messages.add(String.format("The value in position %d in 'exp' field is not between the specified" +
                                                               " boundaries in of entity %d for pathway %d",
                                            k, j, i));
                                }
                            }
                        }
                    }

                    if (entity.getMapsTo() == null || entity.getMapsTo().isEmpty()) {
                        messages.add(String.format("No mapping provided in the entity %d for pathway %d", j, i));
                    } else {
                        for (int k = 1; k <= entity.getMapsTo().size(); k++) {
                            final ExternalMainIdentifier mi = entity.getMapsTo().get(k - 1);
                            if (mi.getId() == null || mi.getId().isEmpty()) {
                                messages.add(String.format(
                                        "Identifier 'null' for the mapping %d of entity %d for pathway %d",
                                        k, j, i));
                            }
                            if (!isValidResource(mi.getResource())) {
                                messages.add(String.format("'%s' for the mapping %d of entity %d for pathway %d is not a valid MainResource",
                                        mi.getResource(), k, j, i));
                            }
                        }
                    }
                }
            }

            if (interactors) { //Interactors check
                for (int j = 1; j <= data.getInteractors().size(); j++) {
                    final ExternalInteractor interactor = data.getInteractors().get(j - 1);
                    if (interactor.getId() == null || interactor.getId().isEmpty())
                        messages.add(String.format("No identifier provided for interactor %d of pathway %d", j, i));

                    int expValues = expressionSummary.getExpValues();
                    List<Double> exp = interactor.getExp();
                    if (expValues == 0) {
                        if (exp != null && exp.size() > 0) {
                            messages.add(String.format("'exp' field must not contain any value in the interactor %d for pathway %d", j, i));
                        }
                    } else {
                        int aux = (exp == null) ? 0 : exp.size();
                        if (aux != expValues) {
                            messages.add(String.format("'exp' field contains %d values but it must contain %d" +
                                                       " values in the interactor %d for pathway %d", aux, expValues, j, i));
                        } else {
                            for (int k = 1; j <= exp.size(); j++) {
                                final Double value = exp.get(k - 1);
                                if (value == null) {
                                    messages.add(String.format("The value in position %d in 'exp' field is null in interactor %d for pathway %d",
                                            k, j, i));
                                } else if (!expressionSummary.isValid(value)) {
                                    messages.add(String.format("The value in position %d in 'exp' field is not between the specified" +
                                                               " boundaries in of interactor %d for pathway %d",
                                            k, j, i));
                                }
                            }
                        }
                    }

                    if (interactor.getMapsTo() == null || interactor.getMapsTo().isEmpty()) {
                        messages.add(String.format("No mapping provided in the interactor %d for pathway %d", j, i));
                    } else {
                        for (int k = 1; k <= interactor.getMapsTo().size(); k++) {
                            final ExternalInteraction in = interactor.getMapsTo().get(k - 1);
                            if (in.getId() == null || in.getId().isEmpty()) {
                                messages.add(String.format("Identifier 'null' for the mapping %d of interactor %d for pathway %d", k, j, i));
                            }
                            if (in.getInteractsWith() == null || in.getInteractsWith().isEmpty()) {
                                messages.add(String.format("No interacts with provided for mapping %d" +
                                                           " in the interactor %d for pathway %d", k, j, i));
                            } else {
                                for (int l = 1; l <= in.getInteractsWith().size(); l++) {
                                    ExternalMainIdentifier mi = in.getInteractsWith().get(l - 1);
                                    if (mi.getId() == null || mi.getId().isEmpty()) {
                                        messages.add(String.format("Identifier 'null' for the interactsWith %d of the maps to %d " +
                                                                   "of entity %d for pathway %d", l, k, j, i));
                                    }
                                    if (!isValidResource(mi.getResource())) {
                                        messages.add(String.format("'%s' for the interactsWith %d of the maps to %d of interactor %d " +
                                                                   "for pathway %d is not a valid MainResource", mi.getResource(), l, k, j, i));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //Reactions check
        if (data.getReactions() == null || data.getReactions().isEmpty()) {
            messages.add(String.format("'reactions' not found for pathway %d", i));
        } else {
            for (int j = 1; j <= data.getReactions().size(); j++) {
                ExternalAnalysisReaction rxn = data.getReactions().get(j -1);
                if (rxn.getDbId() == null)
                    messages.add(String.format("No identifier provided for reaction %d of pathway %d", j, i));

                if (rxn.getStId() == null || rxn.getStId().isEmpty()) {
                    messages.add(String.format("No stable identifier provided for reaction %d of pathway %d", j, i));
                } else if (!STID_PATTERN.matcher(rxn.getStId()).matches()) {
                    messages.add(String.format("'%s' stId provided for reaction %d of pathway %d is a wrong stable identifier", rxn.getStId(), j, i));
                }

                if (rxn.getResources() == null || rxn.getResources().isEmpty()) {
                    messages.add(String.format("No resources provided for reaction %d of pathway %d", j, i));
                } else {
                    for (String resource : rxn.getResources()) {
                        if (!isValidResource(resource))
                            messages.add(String.format("'%s' is not a valid resources for reaction %d of pathway %d", resource, j, i));
                    }
                }
            }
        }
    }

    private void checkPathwayNodeStatistics(ExternalStatistics statistic, ExternalExpressionSummary expressionSummary, int i, List<String> messages) {
        for (Field field : statistic.getClass().getDeclaredFields()) {
            if (!field.getName().equals("exp")) {
                Object val = null;
                try {
                    String method = "get" + StringUtils.capitalize(field.getName());
                    val = statistic.getClass().getMethod(method).invoke(statistic);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    //nothing here
                } finally {
                    if (val == null) {
                        messages.add(String.format(
                                "'%s' not specified in the statistics (resource:%s) for pathway %d",
                                field.getName(), statistic.getResource(), i));
                    }
                }
            }
        }
        if (statistic.getResource() != null) {
            if (!isValidResource(statistic.getResource())) {
                messages.add(String.format("'%s' in the statistics for pathway %d is not a valid MainResource", statistic.getResource(), i));
            }
        }


        int expValues = expressionSummary.getExpValues();
        List<Double> exp = statistic.getExp();
        if (expValues == 0) {
            if (exp != null && exp.size() > 0) {
                messages.add(String.format(
                        "'exp' field must not contain any value in the statistics (resource:%s) for pathway %d",
                        statistic.getResource(), i));
            }
        } else {
            int aux = (exp == null) ? 0 : exp.size();
            if (aux != expValues) {
                messages.add(String.format(
                        "'exp' field contains %d values but it must contain %d values in the statistics (resource:%s) for pathway %d",
                        aux, expValues, statistic.getResource(), i));
            } else {
                for (int j = 1; j <= exp.size(); j++) {
                    final Double value = exp.get(j - 1);
                    if (value == null) {
                        messages.add(String.format("The value in position %d in 'exp' field is null in the statics (resource:%s) for pathway %d",
                                j, statistic.getResource(), i));
                    } else if (!expressionSummary.isValid(value)) {
                        messages.add(String.format("The value in position %d in 'exp' field is not between the specified " +
                                                   "boundaries in the statics (resource:%s) for pathway %d",
                                j, statistic.getResource(), i));
                    }
                }
            }
        }
    }

    private void checkSpecies(ExternalSpeciesNode species, int i, List<String> messages) {
        for (Field field : species.getClass().getDeclaredFields()) {
            Object val = null;
            try {
                String method = "get" + StringUtils.capitalize(field.getName());
                val = species.getClass().getMethod(method).invoke(species);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                //nothing here
            } finally {
                if (val == null) {
                    messages.add(String.format("'%s' not specified in the species for pathway %d", field.getName(), i));
                }
            }
        }
    }

    private void checkNotFound(List<ExternalIdentifier> notFound, ExternalExpressionSummary expressionSummary, List<String> messages) {

        for (int i = 1; i <= notFound.size(); i++) {
            ExternalIdentifier identifier = notFound.get(i - 1);

            if(identifier.getId() == null || identifier.getId().isEmpty()){
                messages.add(String.format("'id' field not specified for the not found entity %d", i));
            }

            int expValues = expressionSummary.getExpValues();
            List<Double> exp = identifier.getExp();
            if (expValues == 0) {
                if (exp != null && exp.size() > 0) {
                    messages.add(String.format("'exp' field must not contain any value in the not found entity %d", i));
                }
            } else {
                int aux = (exp == null) ? 0 : exp.size();
                if (aux != expValues) {
                    messages.add(String.format(
                            "'exp' field contains %d values but it must contain %d values in the not found entity %d", aux, expValues, i));
                } else {
                    for (int j = 1; j <= exp.size(); j++) {
                        final Double value = exp.get(j - 1);
                        if (value == null) {
                            messages.add(String.format("The value in position %d in 'exp' field is null in not found entity %d", j, i));
                        } else if (!expressionSummary.isValid(value)) {
                            messages.add(String.format("The value in position %d in 'exp' field is not between " +
                                                       "the specified boundaries in the not found entity %d", j, i));
                        }
                    }
                }
            }
        }
    }


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isValidResource(String resource) {
        if (resource.equals("TOTAL")) return true;
        return ResourceFactory.getMainResource(resource) != null;
    }

    @Autowired
    public void setAnalysisData(AnalysisData analysisData) {
        this.analysisData = analysisData;
    }

}
