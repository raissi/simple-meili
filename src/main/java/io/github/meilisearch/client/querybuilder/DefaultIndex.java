package io.github.meilisearch.client.querybuilder;

import io.github.meilisearch.client.querybuilder.delete.DefaultDeleteIndex;
import io.github.meilisearch.client.querybuilder.delete.DeleteIndex;

public class DefaultIndex implements Index {
    private final String index;

    public DefaultIndex(String index) {
        this.index = index;
    }

    @Override
    public DeleteIndex delete() {
        return new DefaultDeleteIndex(this.index);
    }
}
