package org.raissi.meilisearch.client.querybuilder.search;

@FunctionalInterface
public interface JsonWriter {
    String json(Object body);
}
