package io.github.meilisearch.client.querybuilder;

import io.github.meilisearch.client.querybuilder.delete.DeleteIndex;

public interface Index {
    DeleteIndex delete();
}
