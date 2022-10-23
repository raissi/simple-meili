package org.raissi.meilisearch.client.querybuilder.search;

import org.raissi.meilisearch.client.querybuilder.MeiliRequest;

import java.util.List;

public interface SearchRequest<SelfT extends SearchRequest<SelfT>> extends MeiliRequest {

    SelfT fetchOnly(List<String> fields);

    String fields();
}
