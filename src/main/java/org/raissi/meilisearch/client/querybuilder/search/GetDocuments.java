package org.raissi.meilisearch.client.querybuilder.search;

public interface GetDocuments extends SearchRequest<GetDocuments> {
    GetDocuments startingAt(int offset);
    GetDocuments fetch(int limit);

    int limit();

    int offset();
}
