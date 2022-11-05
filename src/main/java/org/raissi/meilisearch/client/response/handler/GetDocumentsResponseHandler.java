package org.raissi.meilisearch.client.response.handler;

import org.raissi.meilisearch.client.querybuilder.search.GetDocuments;
import org.raissi.meilisearch.client.response.exceptions.MeiliSearchException;
import org.raissi.meilisearch.client.response.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GetDocumentsResponseHandler implements ResponseHandler {

    private static final Logger logger = LoggerFactory.getLogger(GetDocumentsResponseHandler.class);
    private final GetDocuments get;

    public GetDocumentsResponseHandler(GetDocuments get) {
        this.get = get;
    }

    @Override
    public Optional<MeiliSearchException> buildException(int code, String calledResource, Map<String, List<String>> responseHeaders, String respBody) {
        if(code == 404) {
            logger.error("404 - Could not find documents at: '{}'. Server responded with: {}", get.path(), respBody);
            return Optional.of(new NotFoundException(calledResource, respBody));
        }
        return Optional.of(new MeiliSearchException("Got error "+code+" not yet handled. Body is: "+respBody));
    }
}
