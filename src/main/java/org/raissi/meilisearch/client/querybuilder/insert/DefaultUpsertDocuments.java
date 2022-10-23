package org.raissi.meilisearch.client.querybuilder.insert;

import java.util.List;

public class DefaultUpsertDocuments<T> extends BaseWrite<T> implements UpsertDocuments<T>  {

    public DefaultUpsertDocuments(String index, List<T> documents) {
        super(index, documents);
    }

    @Override
    public UpsertDocuments<T> withPrimaryKey(String primaryKey) {
        super.primaryKey = primaryKey;
        return this;
    }
}
