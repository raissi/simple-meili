package io.github.meilisearch.client.querybuilder.search;

import io.github.meilisearch.client.querybuilder.MeiliRequest;

import java.util.List;

public interface FetchFieldsRequest<SelfT extends FetchFieldsRequest<SelfT>> extends MeiliRequest {

    SelfT fetchOnly(List<String> fields);

    String fields();
}
