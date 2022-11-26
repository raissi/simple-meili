package io.github.meilisearch.client;

import io.github.meilisearch.client.querybuilder.MeiliQueryBuilder;
import io.github.meilisearch.client.querybuilder.WriteCanDefinePrimaryKey;
import io.github.meilisearch.client.querybuilder.delete.DeleteAllDocuments;
import io.github.meilisearch.client.querybuilder.delete.DeleteDocumentsByIds;
import io.github.meilisearch.client.querybuilder.delete.DeleteIndex;
import io.github.meilisearch.client.querybuilder.delete.DeleteOneDocument;
import io.github.meilisearch.client.querybuilder.insert.OverrideDocuments;
import io.github.meilisearch.client.querybuilder.insert.UpsertDocuments;
import io.github.meilisearch.client.querybuilder.insert.WriteRequest;
import io.github.meilisearch.client.querybuilder.search.*;
import io.github.meilisearch.client.querybuilder.tasks.GetTask;
import io.github.meilisearch.client.response.exceptions.MeiliSearchException;
import io.github.meilisearch.client.response.handler.*;
import io.github.meilisearch.client.response.model.GetResults;
import io.github.meilisearch.client.response.model.MeiliTask;
import io.github.meilisearch.client.response.model.MeiliWriteResponse;
import io.github.meilisearch.client.response.model.SearchResponse;
import io.github.meilisearch.control.Try;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

public class MeiliClientOkHttp implements MeiliClient {

    private static final Logger logger = LoggerFactory.getLogger(MeiliClientOkHttp.class);
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String BEARER = "Bearer ";

    private final OkHttpClient okHttpClient;
    private final String meiliSearchHost;
    private final String searchKey;

    private final JsonWriter jsonWriter;
    private final JsonReader jsonReader;

    public MeiliClientOkHttp(OkHttpClient okHttpClient, String meiliSearchHost, String searchKey, JsonWriter jsonWriter, JsonReader jsonReader) {
        this.okHttpClient = okHttpClient;
        this.meiliSearchHost = meiliSearchHost;
        this.searchKey = searchKey;
        this.jsonWriter = jsonWriter;
        this.jsonReader = jsonReader;
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
                .andThenTry(responseBody -> jsonReader.readValue(responseBody, MeiliTask.class));
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
    public <T> Try<GetResults<T>> get(GetDocuments get, Class<T> resultType) {
        return get(get)
                .andThenTry(responseBody -> jsonReader.parseGetResults(responseBody, resultType));
    }

    @Override
    public <T> Try<T> get(GetDocument get, Class<T> resultType) {
        return get(get)
                .andThenTry(responseBody -> jsonReader.readValue(responseBody, resultType));
    }

    @Override
    public <T> Try<Optional<T>> get(GetDocumentIgnoreNotFound get, Class<T> resultType) {
        return get(get)
                .andThenTry(responseBody -> {
                    if (responseBody.isPresent() ){
                        return Optional.of(jsonReader.readValue(responseBody.orElse(null), resultType));
                    }
                    return Optional.empty();
                });
    }

    @Override
    public <T> Try<SearchResponse<T>> search(SearchRequest request, Class<T> resultType) {
        return search(request)
                .andThenTry(rawResponse -> jsonReader.parseSearchResults(rawResponse, resultType));
    }
    @Override
    public Try<String> search(SearchRequest searchRequest) {
        String path = searchRequest.path();
        HttpUrl url = url(Map.of(), path);

        String searchJson = searchRequest.json(jsonWriter);
        logger.debug("Searching in {}, query: {}", url, searchJson);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", MeiliClientOkHttp.BEARER + searchKey)
                .post(RequestBody.create(searchJson, JSON))
                .build();

        return Try.of(() -> executeAndThrowIfEmptyException(request, new DefaultMeiliErrorHandler(searchRequest)));
    }

    //TODO check whether to overload with a callback method
    @Override
    public <T> Try<CanBlockOnTask> override(OverrideDocuments<T> override) {
        Function<Request.Builder, Request> methodBuilder = builder -> builder.post(RequestBody.create(override.json(jsonWriter), JSON))
                .build();
        return write(override, methodBuilder);
    }

    @Override
    public Try<CanBlockOnTask> upsert(UpsertDocuments upsert) {
        Function<Request.Builder, Request> methodBuilder = builder -> builder.put(RequestBody.create(upsert.json(jsonWriter), JSON))
                .build();
        return write(upsert, methodBuilder);
    }

    @Override
    public Try<CanBlockOnTask> delete(DeleteIndex deleteIndex) {
        Function<Request.Builder, Request> methodBuilder = builder -> builder.delete()
                .build();
        return write(deleteIndex, methodBuilder, Map.of());
    }

    @Override
    public Try<CanBlockOnTask> deleteIndex(String index) {
        DeleteIndex delete = MeiliQueryBuilder.index(index).delete();
        return delete(delete);
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
                            builder.post(RequestBody.create(deleteByIds.json(jsonWriter), JSON))
                                    .build();
        return write(deleteByIds, methodBuilder, Map.of());
    }

    @Override
    public <T> Try<CanBlockOnTask> deleteByIds(String index, Collection<T> ids) {
        DeleteDocumentsByIds byIds = MeiliQueryBuilder.fromIndex(index).delete(ids);
        return deleteByIds(byIds);
    }

    private <X extends WriteCanDefinePrimaryKey<X>> Try<CanBlockOnTask> write(WriteCanDefinePrimaryKey<X> write,
                                                                                    Function<Request.Builder, Request> methodBuilder) {
        Map<String, String> params = write.primaryKey()
                .map(p -> Map.of("primaryKey", p))
                .orElse(Map.of());
        return write(write, methodBuilder, params);
    }

    private Try<CanBlockOnTask> write(WriteRequest write,
                                      Function<Request.Builder, Request> methodBuilder,
                                      Map<String, String> params) {
        String path = write.path();
        HttpUrl url = url(params, path);

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", BEARER + searchKey);

        Request request = methodBuilder.apply(requestBuilder);

        return Try.of(() -> executeAndThrowIfEmptyException(request, new DefaultMeiliErrorHandler(write)))
                .andThenTry(rawResponse -> jsonReader.readValue(rawResponse, MeiliWriteResponse.class))
                .andThenTry(writeResponse -> new SimpleBlockingCapableTaskHandler(writeResponse, this));
    }


    private <G extends FetchFieldsRequest<G>> Try<String> get(FetchFieldsRequest<G> get, ResponseHandler responseHandler) {
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

    static class MeiliClientOkHttpBuilder implements MeiliClientOkHttp.WithOkHttpClient,
            WithHostSet<MeiliClientOkHttp>,
            WithCustomJsonWriter<MeiliClientOkHttp>,
            WithCustomJsonReader<MeiliClientOkHttp> {
        private final OkHttpClient okHttpClient;
        private String meilisearchHost;

        private JsonWriter jsonWriter;
        private JsonReader jsonReader;

        MeiliClientOkHttpBuilder(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
        }

        @Override
        public WithHostSet<MeiliClientOkHttp> forHost(String hostUrl) {
            this.meilisearchHost = hostUrl;
            return this;
        }

        @Override
        public WithCustomJsonWriter<MeiliClientOkHttp> withJsonWriter(JsonWriter jsonWriter) {
            this.jsonWriter = jsonWriter;
            return this;
        }

        @Override
        public MeiliClientOkHttp withSearchKey(String key) {

            return new MeiliClientOkHttp(this.okHttpClient,
                    this.meilisearchHost,
                    key,
                    ofNullable(jsonWriter).orElseGet(JacksonJsonReaderWriter::new),
                    ofNullable(jsonReader).orElseGet(JacksonJsonReaderWriter::new));
        }

        @Override
        public WithCustomJsonReader<MeiliClientOkHttp> andJsonReader(JsonReader jsonReader) {
            this.jsonReader = jsonReader;
            return this;
        }
    }
    public interface WithOkHttpClient {
        WithHostSet<MeiliClientOkHttp> forHost(String hostUrl);
    }

    public interface WithCustomJsonWriter<CLIENT> {
        WithCustomJsonReader<CLIENT> andJsonReader(JsonReader jsonReader);
    }

    public interface WithCustomJsonReader<CLIENT> {
        CLIENT withSearchKey(String key);
    }

    public interface WithHostSet<CLIENT> {
        CLIENT withSearchKey(String key);
        WithCustomJsonWriter<CLIENT> withJsonWriter(JsonWriter jsonWriter);
    }


}
