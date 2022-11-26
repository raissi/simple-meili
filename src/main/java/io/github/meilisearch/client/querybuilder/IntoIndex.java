package io.github.meilisearch.client.querybuilder;

import io.github.meilisearch.client.MeiliClient;
import io.github.meilisearch.client.querybuilder.insert.OverrideDocuments;
import io.github.meilisearch.client.querybuilder.insert.UpsertDocuments;

import java.util.List;

public interface IntoIndex {
    /**
     * Add a list of documents or replace them if they already exist. If the provided index does not exist, it will be created.<br/>
     * If you send an already existing document (same document id) the whole existing document will be overwritten by the new document.
     * Fields that are no longer present in the new document are removed.
     *
     * @see <a href="https://docs.meilisearch.com/reference/api/documents.html#add-or-replace-documents">...</a>
     * @param documents the documents to be written
     * @return A wrapper containing the documents and ready to be executed by
     *          {@link MeiliClient#override(OverrideDocuments)}
     * @param <T> Type of the documents
     */
    <T> OverrideDocuments<T> overrideDocuments(List<T> documents);

    /**
     * Add a list of documents or update them if they already exist. If the provided index does not exist, it will be created.<br/>
     * If you send an already existing document (same document id) the old document will be only partially updated according
     * to the fields of the new document. Thus, any fields not present in the new document are kept and remain unchanged.
     * @see <a href="https://docs.meilisearch.com/reference/api/documents.html#add-or-update-documents">Add or update documents Docs</a>
     * @param documents the documents to be inserted or updated
     * @return A wrapper containing the documents and ready to be executed by
     *          {@link MeiliClient#upsert(UpsertDocuments)}
     * @param <T> Type of the documents
     */
    <T> UpsertDocuments upsertDocuments(List<T> documents);
}
