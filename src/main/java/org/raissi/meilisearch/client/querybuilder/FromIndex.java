package org.raissi.meilisearch.client.querybuilder;

import org.raissi.meilisearch.client.querybuilder.delete.DeleteAllDocuments;
import org.raissi.meilisearch.client.querybuilder.delete.DeleteDocumentsByIds;
import org.raissi.meilisearch.client.querybuilder.delete.DeleteOneDocument;
import org.raissi.meilisearch.client.querybuilder.search.GetDocument;
import org.raissi.meilisearch.client.querybuilder.search.GetDocuments;

import java.util.Collection;

public interface FromIndex {

    GetDocuments get();
    GetDocument get(String documentId);

    DeleteAllDocuments deleteAll();
    <T> DeleteOneDocument delete(T uid);

    <T> DeleteDocumentsByIds delete(Collection<T> ids);
}
