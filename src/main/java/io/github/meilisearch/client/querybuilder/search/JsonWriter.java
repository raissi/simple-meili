package io.github.meilisearch.client.querybuilder.search;


/**
 * Implementations of this interface MUST be thread safe
 */
@FunctionalInterface
public interface JsonWriter {
    String json(Object body);
}
