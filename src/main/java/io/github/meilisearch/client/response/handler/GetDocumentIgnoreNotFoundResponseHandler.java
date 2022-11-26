package io.github.meilisearch.client.response.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.meilisearch.client.querybuilder.search.GetDocumentIgnoreNotFound;
import io.github.meilisearch.client.response.exceptions.MeiliSearchException;
import io.github.meilisearch.client.response.exceptions.NotFoundException;
import io.github.meilisearch.client.response.model.MeiliError;
import io.github.meilisearch.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.github.meilisearch.client.response.model.MeiliError.DOCUMENT_NOT_FOUND_ERROR;

public class GetDocumentIgnoreNotFoundResponseHandler implements ResponseHandler {

    private final ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(GetDocumentsResponseHandler.class);
    private final GetDocumentIgnoreNotFound get;

    public GetDocumentIgnoreNotFoundResponseHandler(GetDocumentIgnoreNotFound get) {
        this(get, new ObjectMapper());
    }

    public GetDocumentIgnoreNotFoundResponseHandler(GetDocumentIgnoreNotFound get, ObjectMapper objectMapper) {
        this.get = get;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<MeiliSearchException> buildException(int code, String calledResource, Map<String, List<String>> responseHeaders, String respBody) {
        if(code == 404) {
            Try<MeiliError> parsedError = Try.of(() -> objectMapper.readValue(respBody, MeiliError.class));
            logger.error("404 - Could not find document(s) at: {}. Server responded with: {}", get.path(), respBody);
            //ignore error only if user requested to and error code is document_not_found
            Boolean isDocumentNotFoundError = parsedError.ignoreErrors()
                    .map(error -> DOCUMENT_NOT_FOUND_ERROR.equals(error.getCode()))
                    .orElse(false);
            if(isDocumentNotFoundError) {
                return Optional.empty();
            }
            return Optional.of(new NotFoundException(calledResource, respBody));
        }
        return Optional.of(new MeiliSearchException("Got error "+code+" not yet handled. Body is: "+respBody));
    }
}
