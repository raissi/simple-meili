package org.raissi.meilisearch.client.response.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.raissi.meilisearch.client.querybuilder.search.GetDocumentIgnoreNotFound;
import org.raissi.meilisearch.client.response.exceptions.MeiliSearchException;
import org.raissi.meilisearch.client.response.exceptions.NotFoundException;
import org.raissi.meilisearch.client.response.model.MeiliError;
import org.raissi.meilisearch.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GetDocumentIgnoreNotFoundResponseHandler implements ResponseHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(GetDocumentsResponseHandler.class);
    private final GetDocumentIgnoreNotFound get;

    public GetDocumentIgnoreNotFoundResponseHandler(GetDocumentIgnoreNotFound get) {
        this.get = get;
    }

    @Override
    public Optional<MeiliSearchException> buildException(int code, String calledResource, Map<String, List<String>> responseHeaders, String respBody) {
        Try<MeiliError> parsedError = Try.of(() -> objectMapper.readValue(respBody, MeiliError.class));

        if(code == 404) {
            logger.error("404 - Could not find document(s) at: {}. Server responded with: {}", get.path(), respBody);
            //ignore error only if user requested to and error code is document_not_found
            Boolean isDocumentNotFoundError = parsedError.ignoreErrors()
                    .map(error -> "document_not_found".equals(error.getCode()))
                    .orElse(false);
            if(isDocumentNotFoundError) {
                return Optional.empty();
            }
            return Optional.of(new NotFoundException(calledResource, respBody));
        }
        return Optional.of(new MeiliSearchException("Got error "+code+" not yet handled. Body is: "+respBody));
    }
}
