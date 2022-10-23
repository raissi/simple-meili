package org.raissi.meilisearch.client.querybuilder.insert;

import java.util.List;

public class DefaultOverrideDocuments<T> extends BaseWrite<T> implements OverrideDocuments<T> {

    public DefaultOverrideDocuments(String index, List<T> documents) {
        super(index, documents);
    }

    @Override
    public OverrideDocuments<T> withPrimaryKey(String primaryKey) {
        super.primaryKey = primaryKey;
        return this;
    }
}
