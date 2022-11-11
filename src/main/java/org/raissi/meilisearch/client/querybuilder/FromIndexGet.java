package org.raissi.meilisearch.client.querybuilder;

import org.raissi.meilisearch.client.querybuilder.search.GetDocument;
import org.raissi.meilisearch.client.querybuilder.search.GetDocuments;
import org.raissi.meilisearch.client.querybuilder.search.SearchRequest;

public interface FromIndexGet {

    GetDocuments get();
    GetDocument get(String documentId);

}
