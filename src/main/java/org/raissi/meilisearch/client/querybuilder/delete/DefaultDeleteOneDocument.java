package org.raissi.meilisearch.client.querybuilder.delete;

import org.raissi.meilisearch.client.querybuilder.insert.BaseWrite;

public class DefaultDeleteOneDocument<T> extends BaseWrite implements DeleteOneDocument {

    public DefaultDeleteOneDocument(String index, T id) {
        super(index, String.valueOf(id));
    }

}
