package io.github.meilisearch.client.querybuilder.delete;

import io.github.meilisearch.client.querybuilder.insert.WriteRequestWithBody;

public interface DeleteDocumentsByFilter extends WriteRequestWithBody {

    /**
     * Appends filter to the list of filters to be used in delete. <br>
     * See <a href="https://www.meilisearch.com/docs/reference/api/documents#delete-documents-by-filter">Meilisearch Delete By filter</a>
     * @param filter filter to append
     * @return this instance with new filter
     */
    DeleteDocumentsByFilter filter(String filter);

}
