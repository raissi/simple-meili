package org.raissi.meilisearch.client;

import org.raissi.meilisearch.client.querybuilder.insert.OverrideDocuments;
import org.raissi.meilisearch.client.querybuilder.search.GetDocument;
import org.raissi.meilisearch.client.querybuilder.search.GetDocumentIgnoreNotFound;
import org.raissi.meilisearch.client.querybuilder.search.GetDocuments;
import org.raissi.meilisearch.client.querybuilder.tasks.GetTask;
import org.raissi.meilisearch.client.response.SearchResponse;
import org.raissi.meilisearch.client.response.handler.CanBlockOnTask;
import org.raissi.meilisearch.client.response.model.MeiliTask;
import org.raissi.meilisearch.control.Try;

import java.util.Optional;

public interface MeiliClient {


    Try<String> get(GetDocuments get);

    Try<String> get(GetDocument get);

    Try<MeiliTask> get(GetTask get);

    Try<Optional<String>> get(GetDocumentIgnoreNotFound get);

    <T> Try<SearchResponse<T>> get(GetDocuments get, Class<T> resultType);

    <T> Try<T> get(GetDocument get, Class<T> resultType);

    <T> Try<Optional<T>> get(GetDocumentIgnoreNotFound get, Class<T> resultType);

    <T> Try<CanBlockOnTask> override(OverrideDocuments<T> override);

}
