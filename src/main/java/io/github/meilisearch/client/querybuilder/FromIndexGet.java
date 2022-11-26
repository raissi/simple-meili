package io.github.meilisearch.client.querybuilder;

import io.github.meilisearch.client.querybuilder.search.GetDocument;
import io.github.meilisearch.client.querybuilder.search.GetDocuments;

public interface FromIndexGet {

    GetDocuments get();
    GetDocument get(String documentId);

}
