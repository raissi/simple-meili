package org.raissi.meilisearch.client.querybuilder.search;

import java.util.Optional;

public interface GetDocument extends SearchRequest<GetDocument> {


    /**
     * Should not found documents be treated as empty results instead of errors ?
     * Notice that only document_not_found are affected by this.
     * Meaning, if Meilisearch returns document_not_found and this option was enabled
     * then an empty result will be returned. Else, an error is returned
     * @return
     */
    GetDocumentIgnoreNotFound notFoundAsEmpty();
}
