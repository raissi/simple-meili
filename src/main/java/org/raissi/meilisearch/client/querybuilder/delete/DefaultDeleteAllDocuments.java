package org.raissi.meilisearch.client.querybuilder.delete;

import org.raissi.meilisearch.client.querybuilder.insert.BaseWrite;

public class DefaultDeleteAllDocuments extends BaseWrite implements DeleteAllDocuments {

    public DefaultDeleteAllDocuments(String index) {
        super(index);
    }
}
