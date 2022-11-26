package io.github.meilisearch.client.querybuilder.delete;

import io.github.meilisearch.client.querybuilder.insert.BaseWrite;

public class DefaultDeleteAllDocuments extends BaseWrite implements DeleteAllDocuments {

    public DefaultDeleteAllDocuments(String index) {
        super(index);
    }
}
