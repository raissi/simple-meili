package io.github.meilisearch.client.response.handler;

import io.github.meilisearch.client.querybuilder.MeiliRequest;
import io.github.meilisearch.client.response.exceptions.MeiliSearchException;
import io.github.meilisearch.client.response.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DefaultMeiliErrorHandler implements ResponseHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMeiliErrorHandler.class);
    private final MeiliRequest request;

    public DefaultMeiliErrorHandler(MeiliRequest request) {
        this.request = request;
    }

    @Override
    public Optional<MeiliSearchException> buildException(int code, String calledResource, Map<String, List<String>> responseHeaders, String respBody) {
        if(code == 404) {
            logger.error("404 - Could not find resource(s) at: {}. Server responded with: {}", request.path(), respBody);
            return Optional.of(new NotFoundException(calledResource, respBody));
        }
        logger.error("{} - Could not execute request at: {}. Server responded with: {}", code, request.path(), respBody);
        return Optional.of(new MeiliSearchException("Got error "+code+" not yet handled. Body is: "+respBody));
    }
}
