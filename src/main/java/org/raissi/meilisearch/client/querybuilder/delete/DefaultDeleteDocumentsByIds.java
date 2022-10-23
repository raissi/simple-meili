package org.raissi.meilisearch.client.querybuilder.delete;

import org.raissi.meilisearch.client.querybuilder.insert.WriteWithBody;

import java.util.List;

public class DefaultDeleteDocumentsByIds extends WriteWithBody<String> implements DeleteDocumentsByIds {

    public DefaultDeleteDocumentsByIds(String index, List<String> documentIds) {
        super(index, documentIds);
    }

}
