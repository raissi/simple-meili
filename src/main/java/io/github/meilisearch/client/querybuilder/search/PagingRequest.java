package io.github.meilisearch.client.querybuilder.search;

public interface PagingRequest<SelfT extends PagingRequest<SelfT>> extends FetchFieldsRequest<SelfT> {

    SelfT startingAt(int offset);
    SelfT fetch(int limit);

    int limit();

    int offset();

}
