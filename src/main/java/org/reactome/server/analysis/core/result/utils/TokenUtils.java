package org.reactome.server.analysis.core.result.utils;

import org.reactome.server.analysis.core.model.AnalysisType;
import org.reactome.server.analysis.core.model.SpeciesNode;
import org.reactome.server.analysis.core.result.AnalysisStoredResult;
import org.reactome.server.analysis.core.result.exception.ResourceGoneException;
import org.reactome.server.analysis.core.result.exception.ResourceNotFoundException;
import org.reactome.server.analysis.core.result.model.AnalysisSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@Component
public class TokenUtils {

    private static final Logger logger = LoggerFactory.getLogger("tokenLogger");

    private String pathDirectory;

    public TokenUtils() {
    }

    public TokenUtils(String pathDirectory) {
        this.pathDirectory = pathDirectory;
    }

    public void setPathDirectory(String pathDirectory) {
        this.pathDirectory = pathDirectory;
    }

    public AnalysisStoredResult getFromToken(String token) {
        String fileName = getFileName(token);
        if (fileName != null) {
            try {
                return ResultDataUtils.getAnalysisResult(fileName);
            } catch (FileNotFoundException e) {
                //should be alive is only true when the token follows the rule and the resulting date is in the last 7 days
                if (Tokenizer.shouldBeAlive(token)) {
                    throw new ResourceGoneException();
                }
            }
        }
        throw new ResourceNotFoundException();
    }

    public AnalysisSummary getAnalysisSummary(String token, Boolean projection, Boolean interactors, String sampleName, AnalysisType type, String userFileName, String serverName) {
        if (userFileName != null && !userFileName.isEmpty()) {
            return new AnalysisSummary(token, projection, interactors, sampleName, type, userFileName, serverName);
        } else {
            return new AnalysisSummary(token, projection, interactors, sampleName, type, true, serverName);
        }
    }

    public String getFakedMD5(SpeciesNode speciesFrom, SpeciesNode speciesTo) {
        return AnalysisType.SPECIES_COMPARISON.toString() + speciesFrom.getSpeciesID() + "-" + speciesTo.getSpeciesID();
    }

    public String getFileName(String token) {
        String name = Tokenizer.getName(token);
        return String.format("%s/res_%s.bin", this.pathDirectory, name);
    }

    public void saveResult(final AnalysisStoredResult result) {
        String fileName = getFileName(result.getSummary().getToken());
        ResultDataUtils.kryoSerialisation(result, fileName);
    }

}
