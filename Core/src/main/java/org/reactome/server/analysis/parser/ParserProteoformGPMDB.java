package org.reactome.server.analysis.parser;

import org.reactome.server.analysis.core.model.Proteoform;
import org.reactome.server.analysis.parser.exception.ParserException;

import java.io.IOException;

/**
 * Class used to process files in with the GMPDB nomenclature for the description of protein sequence modifications.
 * More information: http://wiki.thegpm.org/wiki/Nomenclature_for_the_description_of_protein_sequence_modifications
 */
public class ParserProteoformGPMDB extends Parser{

    /**
     *
     * @param line
     * @param lineNumber
     * @return
     */

    public static Proteoform getProteoform(String line, int lineNumber) {
        return null;
    }

    public static Proteoform getProteoform(String line) {
        return getProteoform(line, 0);
    }

    @Override
    public boolean flexibleCheck() {
        return false;
    }

    @Override
    public void parseData(String input) throws ParserException {

    }
}
