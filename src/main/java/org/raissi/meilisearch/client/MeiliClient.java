package org.raissi.meilisearch.client;

import org.raissi.meilisearch.client.querybuilder.delete.DeleteAllDocuments;
import org.raissi.meilisearch.client.querybuilder.delete.DeleteDocumentsByIds;
import org.raissi.meilisearch.client.querybuilder.delete.DeleteIndex;
import org.raissi.meilisearch.client.querybuilder.delete.DeleteOneDocument;
import org.raissi.meilisearch.client.querybuilder.insert.OverrideDocuments;
import org.raissi.meilisearch.client.querybuilder.insert.UpsertDocuments;
import org.raissi.meilisearch.client.querybuilder.search.GetDocument;
import org.raissi.meilisearch.client.querybuilder.search.GetDocumentIgnoreNotFound;
import org.raissi.meilisearch.client.querybuilder.search.GetDocuments;
import org.raissi.meilisearch.client.querybuilder.search.SearchRequest;
import org.raissi.meilisearch.client.querybuilder.tasks.GetTask;
import org.raissi.meilisearch.client.response.handler.CanBlockOnTask;
import org.raissi.meilisearch.client.response.model.MeiliTask;
import org.raissi.meilisearch.client.response.model.GetResults;
import org.raissi.meilisearch.client.response.model.SearchResponse;
import org.raissi.meilisearch.control.Try;

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

    <T> Try<CanBlockOnTask> upsert(UpsertDocuments<T> upsert);

    Try<CanBlockOnTask> delete(DeleteIndex deleteIndex);

    Try<CanBlockOnTask> deleteIndex(String index);

    <T> Try<CanBlockOnTask> deleteOne(String index, T id);

    Try<CanBlockOnTask> deleteOne(DeleteOneDocument deleteOne);

    <T> Try<CanBlockOnTask> deleteAll(DeleteAllDocuments deleteAll);

    <T> Try<CanBlockOnTask> deleteAll(String index);

    <T> Try<CanBlockOnTask> deleteByIds(DeleteDocumentsByIds deleteByIds);

    <T> Try<CanBlockOnTask> deleteByIds(String index, Collection<T> ids);
}
