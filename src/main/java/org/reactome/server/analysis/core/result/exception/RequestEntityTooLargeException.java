package org.reactome.server.analysis.core.result.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
//Do NOT annotate it with "ResponseStatus" because it is treated in "HandlerExceptionResolverImpl"
public final class RequestEntityTooLargeException extends AnalysisServiceException {

    public RequestEntityTooLargeException() {
        super(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE);
    }

}
