package io.github.meilisearch.client.querybuilder.search;

import io.github.meilisearch.client.response.model.SearchResponse;
import io.github.meilisearch.client.response.model.GetResults;

/**
 * Implementations of this interface MUST be thread safe
 */
public interface JsonReader {

    <T> T readValue(String content, Class<T> valueType) throws Exception;
    <T> GetResults<T> parseGetResults(String responseBody, Class<T> resultType) throws Exception;

    <T> SearchResponse<T> parseSearchResults(String responseBody, Class<T> resultType) throws Exception;
}
