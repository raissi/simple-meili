package io.github.meilisearch.client.response.handler;

import io.github.meilisearch.client.response.exceptions.MeiliSearchException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ResponseHandler {
    Optional<MeiliSearchException> buildException(int code, String calledResource, Map<String, List<String>> responseHeaders, String respBody);
}
