package org.reactome.server.analysis.core.result.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
//Do NOT annotate it with "ResponseStatus" because it is treated in "HandlerExceptionResolverImpl"
public final class ResourceGoneException extends AnalysisServiceException {

    public ResourceGoneException() {
        super(HttpStatus.GONE);
    }

}
