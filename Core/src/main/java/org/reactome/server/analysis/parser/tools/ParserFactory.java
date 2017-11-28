package org.reactome.server.analysis.parser.tools;

import org.reactome.server.analysis.parser.*;

public class ParserFactory {

    public static Parser createParser(String input) {
        if (ParserProteoformPRO.check(input)) {
            return new ParserProteoformPRO();
        } else if (ParserProteoformSimple.check(input)) {
            return new ParserProteoformSimple();
        }
        return new ParserOriginal();
    }
}
