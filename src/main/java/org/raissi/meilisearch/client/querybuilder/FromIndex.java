package org.raissi.meilisearch.client.querybuilder;

import org.raissi.meilisearch.client.querybuilder.delete.DeleteAllDocuments;
import org.raissi.meilisearch.client.querybuilder.delete.DeleteDocumentsByIds;
import org.raissi.meilisearch.client.querybuilder.delete.DeleteOneDocument;
import org.raissi.meilisearch.client.querybuilder.search.GetDocument;
import org.raissi.meilisearch.client.querybuilder.search.GetDocuments;
import org.raissi.meilisearch.client.querybuilder.search.SearchRequest;

import java.util.Collection;

public interface FromIndex {

    GetDocuments get();
    GetDocument get(String documentId);
    SearchRequest q(String q);

    /**
     * Automatically surround the given phrase with {@code \" \"}.
     * If the given phrase does already start with a \" it will be passed as is
     * @param phrase the search phrase
     * @return an instance of {@link SearchRequest}
     */
    SearchRequest phrase(String phrase);
    SearchRequest filter(String filter);
    SearchRequest filters(Collection<String> filters);

    SearchRequest.AroundPoint aroundPoint(double lat, double lon);


    DeleteAllDocuments deleteAll();
    <T> DeleteOneDocument delete(T uid);

    <T> DeleteDocumentsByIds delete(Collection<T> ids);
}
