package org.raissi.meilisearch.client.querybuilder;

import org.raissi.meilisearch.client.querybuilder.delete.DefaultDeleteIndex;
import org.raissi.meilisearch.client.querybuilder.delete.DeleteIndex;

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
