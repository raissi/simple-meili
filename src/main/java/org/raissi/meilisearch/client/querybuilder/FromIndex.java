package org.raissi.meilisearch.client.querybuilder;

import org.raissi.meilisearch.client.querybuilder.search.GetDocument;
import org.raissi.meilisearch.client.querybuilder.search.GetDocuments;

public interface FromIndex {

    GetDocuments get();
    GetDocument get(String documentId);
}
