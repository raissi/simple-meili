package org.raissi.meilisearch.client;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.raissi.meilisearch.client.querybuilder.MeiliQueryBuilder;
import org.raissi.meilisearch.client.querybuilder.WriteCanDefinePrimaryKey;
import org.raissi.meilisearch.client.querybuilder.delete.DeleteAllDocuments;
import org.raissi.meilisearch.client.querybuilder.delete.DeleteDocumentsByIds;
import org.raissi.meilisearch.client.querybuilder.delete.DeleteOneDocument;
import org.raissi.meilisearch.client.querybuilder.insert.OverrideDocuments;
import org.raissi.meilisearch.client.querybuilder.insert.UpsertDocuments;
import org.raissi.meilisearch.client.querybuilder.insert.WriteRequest;
import org.raissi.meilisearch.client.querybuilder.search.GetDocument;
import org.raissi.meilisearch.client.querybuilder.search.GetDocumentIgnoreNotFound;
import org.raissi.meilisearch.client.querybuilder.search.GetDocuments;
import org.raissi.meilisearch.client.querybuilder.search.SearchRequest;
import org.raissi.meilisearch.client.querybuilder.tasks.GetTask;
import org.raissi.meilisearch.client.response.SearchResponse;
import org.raissi.meilisearch.client.response.exceptions.MeiliSearchException;
import org.raissi.meilisearch.client.response.handler.*;
import org.raissi.meilisearch.client.response.model.MeiliTask;
import org.raissi.meilisearch.client.response.model.MeiliWriteResponse;
import org.raissi.meilisearch.client.response.model.SearchResults;
import org.raissi.meilisearch.control.Try;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class MeiliClientOkHttp implements MeiliClient {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String BEARER = "Bearer ";

    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final String meiliSearchHost;
    private final String searchKey;

    public MeiliClientOkHttp(OkHttpClient okHttpClient, String meiliSearchHost, String searchKey) {
        this.okHttpClient = okHttpClient;
        this.meiliSearchHost = meiliSearchHost;
        this.searchKey = searchKey;
    }

    public static WithOkHttpClient usingOkHttp(OkHttpClient okHttpClient) {
        return new MeiliClientOkHttpBuilder(okHttpClient);
    }

    @Override
    public Try<String> get(GetDocuments get) {
        Map<String, String> params = Map.of("fields", get.fields(),
                "offset", String.valueOf(get.offset()),
                "limit", String.valueOf(get.limit()));
        String path = get.path();
        HttpUrl url = url(params, path);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", MeiliClientOkHttp.BEARER + searchKey)
                .build();
        return Try.of(() -> executeAndThrowIfEmptyException(request, new GetDocumentsResponseHandler(get)));
    }

    @Override
    public Try<String> get(GetDocument get) {
        return get(get, new GetDocumentResponseHandler(get));
    }

    @Override
    public Try<MeiliTask> get(GetTask get) {
        return get(get, new DefaultMeiliErrorHandler(get))
                .andThenTry(responseBody -> objectMapper.readValue(responseBody, MeiliTask.class));
    }

    @Override
    public Try<Optional<String>> get(GetDocumentIgnoreNotFound get) {
        Map<String, String> queryParams = Collections.singletonMap("fields", get.fields());
        String path = get.path();

        HttpUrl url = url(queryParams, path);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", MeiliClientOkHttp.BEARER + searchKey)
                .build();

        return Try.of(() -> execute(request, new GetDocumentIgnoreNotFoundResponseHandler(get)));
    }

    @Override
    public <T> Try<SearchResponse<T>> get(GetDocuments get, Class<T> resultType) {
        return get(get)
                .andThenTry(responseBody -> parse(resultType, responseBody));
    }

    @Override
    public <T> Try<T> get(GetDocument get, Class<T> resultType) {
        return get(get)
                .andThenTry(responseBody -> objectMapper.readValue(responseBody, resultType));
    }

    @Override
    public <T> Try<Optional<T>> get(GetDocumentIgnoreNotFound get, Class<T> resultType) {
        return get(get)
                .andThenTry(responseBody -> {
                    if (responseBody.isPresent() ){
                        return Optional.of(objectMapper.readValue(responseBody.orElse(null), resultType));
                    }
                    return Optional.empty();
                });
    }

    //TODO check whether to overload with a callback method

    @Override
    public <T> Try<CanBlockOnTask> override(OverrideDocuments<T> override) {
        Function<Request.Builder, Request> methodBuilder = builder -> builder.post(RequestBody.create(override.json(), JSON))
                .build();
        return write(override, methodBuilder);
    }

    @Override
    public <T> Try<CanBlockOnTask> upsert(UpsertDocuments<T> upsert) {
        Function<Request.Builder, Request> methodBuilder = builder -> builder.put(RequestBody.create(upsert.json(), JSON))
                .build();
        return write(upsert, methodBuilder);
    }

    @Override
    public <T> Try<CanBlockOnTask> deleteOne(String index, T id) {
        DeleteOneDocument one = MeiliQueryBuilder.fromIndex(index).delete(id);
        return deleteOne(one);
    }

    @Override
    public Try<CanBlockOnTask> deleteOne(DeleteOneDocument deleteOne) {
        Function<Request.Builder, Request> methodBuilder = builder -> builder.delete()
                .build();
        return write(deleteOne, methodBuilder, Map.of());
    }

    @Override
    public Try<CanBlockOnTask> deleteAll(DeleteAllDocuments deleteAll) {
        Function<Request.Builder, Request> methodBuilder = builder -> builder.delete()
                .build();
        return write(deleteAll, methodBuilder, Map.of());
    }

    @Override
    public Try<CanBlockOnTask> deleteAll(String index) {
        DeleteAllDocuments deleteAll = MeiliQueryBuilder.fromIndex(index).deleteAll();
        return deleteAll(deleteAll);
    }

    @Override
    public Try<CanBlockOnTask> deleteByIds(DeleteDocumentsByIds deleteByIds) {
        Function<Request.Builder, Request> methodBuilder = builder ->
                            builder.delete(RequestBody.create(deleteByIds.json(), JSON))
                                    .build();
        return write(deleteByIds, methodBuilder, Map.of());
    }

    @Override
    public <T> Try<CanBlockOnTask> deleteByIds(String index, Collection<T> ids) {
        DeleteDocumentsByIds byIds = MeiliQueryBuilder.fromIndex(index).delete(ids);
        return deleteByIds(byIds);
    }

    private <T, X extends WriteCanDefinePrimaryKey<T, X>> Try<CanBlockOnTask> write(WriteCanDefinePrimaryKey<T, X> write, Function<Request.Builder, Request> methodBuilder) {
        Map<String, String> params = write.primaryKey()
                .map(p -> Map.of("primaryKey", p))
                .orElse(Map.of());
        return write(write, methodBuilder, params);
    }

    private Try<CanBlockOnTask> write(WriteRequest override,
                                      Function<Request.Builder, Request> methodBuilder,
                                      Map<String, String> params) {
        String path = override.path();
        HttpUrl url = url(params, path);

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", BEARER + searchKey);

        Request request = methodBuilder.apply(requestBuilder);

        return Try.of(() -> executeAndThrowIfEmptyException(request, new DefaultMeiliErrorHandler(override)))
                .andThenTry(rawResponse -> objectMapper.readValue(rawResponse, MeiliWriteResponse.class))
                .andThenTry(writeResponse -> new SimpleBlockingCapableTaskHandler(writeResponse, this));
    }


    private <G extends SearchRequest<G>> Try<String> get(SearchRequest<G> get, ResponseHandler responseHandler) {
        Map<String, String> queryParams = Collections.singletonMap("fields", get.fields());
        String path = get.path();

        HttpUrl url = url(queryParams, path);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", MeiliClientOkHttp.BEARER + searchKey)
                .build();

        return Try.of(() -> executeAndThrowIfEmptyException(request, responseHandler));
    }


    private HttpUrl url(Map<String, String> queryParams, String path) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(meiliSearchHost + path))
                .newBuilder();
        queryParams.forEach(urlBuilder::addQueryParameter);
        return urlBuilder.build();
    }

    private Optional<String> execute(Request request, ResponseHandler responseHandler) throws Exception {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return  Optional.of(Objects.requireNonNull(response.body()).string());
            }
            //handle error to return either exception or empty
            handleError(responseHandler, response).ifPresent(e -> {
                throw e;
            });
            return Optional.empty();
        }
    }

    /**
     * Call this when you know the {@code responseHandler} will always return an exception
     * @param request the request to execute
     * @param responseHandler the response handler. Must always return an exception
     * @return the result of {@code execute(request, responseHandler)}
     * @throws Exception propagates the same exception from execute.
     *          BUT throws an IllegalStateException if responseHandler did not return an Exception
     */
    private String executeAndThrowIfEmptyException(Request request, ResponseHandler responseHandler) throws Exception {
        return execute(request, responseHandler)
                .orElseThrow(() -> new IllegalStateException(
                        "This is a library bug. " +
                                "GetDocumentResponseHandler must already have returned an exception, " +
                                "which must have been raised in previous execute call"));
    }

    private static Optional<MeiliSearchException> handleError(ResponseHandler responseHandler, Response response) throws IOException {
        String calledResource = response.request().url().toString();
        String respBody = null;
        ResponseBody body = response.body();
        if (body != null) {
            respBody = body.string();
        }
        Map<String, List<String>> responseHeaders = response.headers().toMultimap();
        return responseHandler.buildException(response.code(), calledResource, responseHeaders, respBody);
    }

    private <T> SearchResponse<T> parse(Class<T> resultType, String responseBody) throws Exception {
        JavaType type = objectMapper.getTypeFactory().constructParametricType(SearchResults.class, resultType);
        SearchResults<T> results = objectMapper.readValue(responseBody, type);
        return new SearchResponse<>(results);
    }


    static class MeiliClientOkHttpBuilder implements MeiliClientOkHttp.WithOkHttpClient, WithHostSet {
        private final OkHttpClient okHttpClient;
        private String meilisearchHost;

        MeiliClientOkHttpBuilder(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
        }

        @Override
        public WithHostSet forHost(String hostUrl) {
            this.meilisearchHost = hostUrl;
            return this;
        }

        @Override
        public MeiliClientOkHttp withSearchKey(String key) {
            return new MeiliClientOkHttp(this.okHttpClient,
                    this.meilisearchHost,
                    key);
        }
    }
    public interface WithOkHttpClient {
        WithHostSet forHost(String hostUrl);
    }

    public interface WithHostSet {
        MeiliClientOkHttp withSearchKey(String key);
    }


}
