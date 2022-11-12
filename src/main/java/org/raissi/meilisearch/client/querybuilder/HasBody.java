package org.raissi.meilisearch.client.querybuilder;

import org.raissi.meilisearch.client.querybuilder.search.JsonWriter;

public interface HasBody {
    String json(JsonWriter jsonWriter);
}
