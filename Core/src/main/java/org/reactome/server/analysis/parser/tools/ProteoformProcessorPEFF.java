package org.reactome.server.analysis.parser.tools;

import java.io.File;

/**
 * Utility class to validate and extract information from PEFF files, specification 1.0.draft25
 * The format is currently in development at: https://github.com/HUPO-PSI/PEFF
 * The specification document: https://github.com/HUPO-PSI/PEFF/tree/master/Specification
 *
 * @author Luis Francisco Hernández Sánchez <luis.sanchez@uib.no>
 */

public class ProteoformProcessorPEFF {

    /**
     * Validates if the provided file follows the PEFF format.
     *
     * @param file File to verify
     * @return true if the file is valid according to the format, false if the file is not valid
     */
    public static boolean isValid(File file) {
        return false;
    }

}
