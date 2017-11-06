package org.reactome.server.analysis.parser;

import org.reactome.server.analysis.core.model.AnalysisIdentifier;
import org.reactome.server.analysis.parser.exception.ParserException;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class InputProcessor {

    private Set<AnalysisIdentifier> analysisIdentifierSet = new LinkedHashSet<>();

    /**
     * Fills the data structure in memory that will be send to the analysis methods.
     * @param input The contents of the user input file.
     */
    public abstract void parseData(String input) throws IOException, ParserException;
}
