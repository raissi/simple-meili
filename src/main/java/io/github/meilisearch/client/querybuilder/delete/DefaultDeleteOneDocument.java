package io.github.meilisearch.client.querybuilder.delete;

import io.github.meilisearch.client.querybuilder.insert.BaseWrite;

public class DefaultDeleteOneDocument<T> extends BaseWrite implements DeleteOneDocument {

    public DefaultDeleteOneDocument(String index, T id) {
        super(index, String.valueOf(id));
    }

}
