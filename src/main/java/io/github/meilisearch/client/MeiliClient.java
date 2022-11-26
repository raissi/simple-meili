package io.github.meilisearch.client;

import io.github.meilisearch.client.querybuilder.delete.DeleteIndex;
import io.github.meilisearch.client.querybuilder.insert.OverrideDocuments;
import io.github.meilisearch.client.querybuilder.search.GetDocumentIgnoreNotFound;
import io.github.meilisearch.client.querybuilder.search.GetDocuments;
import io.github.meilisearch.client.querybuilder.search.SearchRequest;
import io.github.meilisearch.client.querybuilder.tasks.GetTask;
import io.github.meilisearch.client.response.handler.CanBlockOnTask;
import io.github.meilisearch.client.response.model.MeiliTask;
import io.github.meilisearch.client.response.model.SearchResponse;
import io.github.meilisearch.client.querybuilder.delete.DeleteAllDocuments;
import io.github.meilisearch.client.querybuilder.delete.DeleteDocumentsByIds;
import io.github.meilisearch.client.querybuilder.delete.DeleteOneDocument;
import io.github.meilisearch.client.querybuilder.insert.UpsertDocuments;
import io.github.meilisearch.client.querybuilder.search.GetDocument;
import io.github.meilisearch.client.response.model.GetResults;
import io.github.meilisearch.control.Try;

import java.util.Collection;
import java.util.Optional;

public interface MeiliClient {

    //TODO split into Admin vs Search Clients


    Try<String> get(GetDocuments get);

    Try<String> get(GetDocument get);

    Try<MeiliTask> get(GetTask get);

    Try<Optional<String>> get(GetDocumentIgnoreNotFound get);

    <T> Try<GetResults<T>> get(GetDocuments get, Class<T> resultType);

    <T> Try<T> get(GetDocument get, Class<T> resultType);

    <T> Try<Optional<T>> get(GetDocumentIgnoreNotFound get, Class<T> resultType);

    <T> Try<SearchResponse<T>> search(SearchRequest request, Class<T> resultType);

    Try<String> search(SearchRequest search);

    <T> Try<CanBlockOnTask> override(OverrideDocuments<T> override);

    Try<CanBlockOnTask> upsert(UpsertDocuments upsert);

    Try<CanBlockOnTask> delete(DeleteIndex deleteIndex);

    Try<CanBlockOnTask> deleteIndex(String index);

    <T> Try<CanBlockOnTask> deleteOne(String index, T id);

    Try<CanBlockOnTask> deleteOne(DeleteOneDocument deleteOne);

    Try<CanBlockOnTask> deleteAll(DeleteAllDocuments deleteAll);

    Try<CanBlockOnTask> deleteAll(String index);

    Try<CanBlockOnTask> deleteByIds(DeleteDocumentsByIds deleteByIds);

    <T> Try<CanBlockOnTask> deleteByIds(String index, Collection<T> ids);
}
