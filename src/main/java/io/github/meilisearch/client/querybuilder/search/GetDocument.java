package io.github.meilisearch.client.querybuilder.search;

public interface GetDocument extends FetchFieldsRequest<GetDocument> {


    /**
     * Should not found documents be treated as empty results instead of errors ?
     * Notice that only document_not_found are affected by this.
     * Meaning, if Meilisearch returns document_not_found and this option was enabled
     * then an empty result will be returned. Else, an error is returned
     * @return
     */
    GetDocumentIgnoreNotFound notFoundAsEmpty();
}
