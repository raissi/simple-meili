package org.raissi.meilisearch.client.querybuilder.search;

import org.raissi.meilisearch.client.querybuilder.MeiliRequest;

import java.util.List;

public interface FetchFieldsRequest<SelfT extends FetchFieldsRequest<SelfT>> extends MeiliRequest {

    SelfT fetchOnly(List<String> fields);

    String fields();
}
