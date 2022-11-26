package io.github.meilisearch.client.querybuilder.delete;

import io.github.meilisearch.client.querybuilder.insert.WriteWithBody;

import java.util.List;

public class DefaultDeleteDocumentsByIds extends WriteWithBody<String> implements DeleteDocumentsByIds {

    public DefaultDeleteDocumentsByIds(String index, List<String> documentIds) {
        super("/indexes/"+index+"/"+"documents/delete-batch", documentIds, true);
    }

}
