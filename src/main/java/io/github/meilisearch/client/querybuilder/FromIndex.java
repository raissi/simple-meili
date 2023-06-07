package io.github.meilisearch.client.querybuilder;

import io.github.meilisearch.client.querybuilder.delete.DeleteDocumentsByFilter;
import io.github.meilisearch.client.querybuilder.search.SearchRequest;
import io.github.meilisearch.client.querybuilder.delete.DeleteAllDocuments;
import io.github.meilisearch.client.querybuilder.delete.DeleteDocumentsByIds;
import io.github.meilisearch.client.querybuilder.delete.DeleteOneDocument;

import java.util.Collection;

public interface FromIndex extends FromIndexSearch<SearchRequest>, FromIndexGet {

    DeleteAllDocuments deleteAll();
    <T> DeleteOneDocument delete(T uid);

    <T> DeleteDocumentsByIds delete(Collection<T> ids);

    DeleteDocumentsByFilter deleteByFilter(String filter);
}
