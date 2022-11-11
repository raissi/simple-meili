package org.raissi.meilisearch.client.querybuilder;

import org.raissi.meilisearch.client.querybuilder.delete.DeleteAllDocuments;
import org.raissi.meilisearch.client.querybuilder.delete.DeleteDocumentsByIds;
import org.raissi.meilisearch.client.querybuilder.delete.DeleteOneDocument;
import org.raissi.meilisearch.client.querybuilder.search.SearchRequest;

import java.util.Collection;

public interface FromIndex extends FromIndexSearch<SearchRequest>, FromIndexGet {

    DeleteAllDocuments deleteAll();
    <T> DeleteOneDocument delete(T uid);

    <T> DeleteDocumentsByIds delete(Collection<T> ids);
}
