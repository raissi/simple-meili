package io.github.meilisearch.client.querybuilder.insert;

import java.util.List;

public class DefaultUpsertDocuments<T> extends WriteWithBody<T> implements UpsertDocuments  {

    public DefaultUpsertDocuments(String index, List<T> documents) {
        super(index, documents);
    }

    @Override
    public UpsertDocuments withPrimaryKey(String primaryKey) {
        super.primaryKey = primaryKey;
        return this;
    }
}
