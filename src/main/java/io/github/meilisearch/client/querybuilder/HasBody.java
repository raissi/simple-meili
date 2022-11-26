package io.github.meilisearch.client.querybuilder;

import io.github.meilisearch.client.querybuilder.search.JsonWriter;

public interface HasBody {
    String json(JsonWriter jsonWriter);
}
