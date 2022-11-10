package org.raissi.meilisearch.integration;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.raissi.meilisearch.client.MeiliClient;
import org.raissi.meilisearch.client.MeiliClientOkHttp;
import org.raissi.meilisearch.client.querybuilder.MeiliQueryBuilder;
import org.raissi.meilisearch.client.querybuilder.insert.UpsertDocuments;
import org.raissi.meilisearch.client.querybuilder.search.SearchRequest;
import org.raissi.meilisearch.client.response.handler.CanBlockOnTask;
import org.raissi.meilisearch.client.response.model.SearchResponse;
import org.raissi.meilisearch.model.Author;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.raissi.meilisearch.client.MeiliClientOkHttp.JSON;


public class SearchDocumentsTest {

    static MeiliClient client;
    static String indexName = "authorsForSearch";

    @BeforeAll
    public static void setUp() throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient();

        client = MeiliClientOkHttp.usingOkHttp(okHttpClient)
                .forHost("http://localhost:7700")
                .withSearchKey("masterKey");

        List<Author> authors = authors();
        UpsertDocuments<Author> upsert = MeiliQueryBuilder.intoIndex(indexName)
                .upsertDocuments(authors)
                .withPrimaryKey("uid");
        client.upsert(upsert)
                .andThen(CanBlockOnTask::waitForCompletion)
                .orElseThrow(Function.identity());

        Request request = new Request.Builder()
                .url("http://localhost:7700/indexes/"+indexName+"/settings/filterable-attributes")
                .addHeader("Authorization", "Bearer masterKey")
                .put(RequestBody.create("[\"country\", \"uid\"]", JSON))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException();
            }
            Thread.sleep(300);
        }
    }

    @AfterAll
    public static void cleanUp() {
        client.deleteIndex(indexName)
                .andThenTry(CanBlockOnTask::waitForCompletion);
    }

    @Test
    void shouldSearchForJane() {

        SearchRequest search = MeiliQueryBuilder.fromIndex(indexName)
                .q("jane");
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        SearchResponse<Author> searchResults = client.search(search, Author.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false))
                .orElse(() -> null);

        Assertions.assertTrue(isSuccess.get(), "Search should execute");
        Assertions.assertEquals(1, searchResults.getEstimatedTotalHits(), "Must find only Jane Austen");
        Assertions.assertEquals(0, searchResults.getOffset());
        Assertions.assertEquals(20, searchResults.getLimit());
    }

    @Test
    void shouldSearchForBothCharles() {

        SearchRequest search = MeiliQueryBuilder.fromIndex(indexName)
                .q("CHARLES");
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        SearchResponse<Author> searchResults = client.search(search, Author.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false))
                .orElse(() -> null);

        Assertions.assertTrue(isSuccess.get(), "Search should execute");
        Assertions.assertEquals(2, searchResults.getEstimatedTotalHits(), "Must find both darwin and dickens");
        Assertions.assertEquals(0, searchResults.getOffset());
        Assertions.assertEquals(20, searchResults.getLimit());
    }

    @Test
    void shouldSearchForAllCharlesEvenIfDickens() {

        SearchRequest search = MeiliQueryBuilder.fromIndex(indexName)
                .q("charles dickens");
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        SearchResponse<Author> searchResults = client.search(search, Author.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false))
                .orElse(() -> null);

        Assertions.assertTrue(isSuccess.get(), "Search should execute");
        Assertions.assertEquals(3, searchResults.getEstimatedTotalHits(), "Must find all charles");
        Assertions.assertEquals(0, searchResults.getOffset());
        Assertions.assertEquals(20, searchResults.getLimit());
    }

    @Test
    void shouldSearchForOnlyCharlesDickens() {

        SearchRequest search = MeiliQueryBuilder.fromIndex(indexName)
                .q("\"CHARLES DICKENS\"");
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        SearchResponse<Author> searchResults = client.search(search, Author.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false))
                .orElse(() -> null);

        Assertions.assertTrue(isSuccess.get(), "Search should execute");
        Assertions.assertEquals(1, searchResults.getEstimatedTotalHits(), "Must find only Dickens");
        Assertions.assertEquals(0, searchResults.getOffset());
        Assertions.assertEquals(20, searchResults.getLimit());
    }

    @Test
    void shouldSearchForOnlyCharlesDickensUsingPhrase() {

        SearchRequest search = MeiliQueryBuilder.fromIndex(indexName)
                .phrase("CHARLES DICKENS");
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        SearchResponse<Author> searchResults = client.search(search, Author.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false))
                .orElse(() -> null);

        Assertions.assertTrue(isSuccess.get(), "Search should execute");
        Assertions.assertEquals(1, searchResults.getEstimatedTotalHits(), "Must find only Dickens");
        Assertions.assertEquals(0, searchResults.getOffset());
        Assertions.assertEquals(20, searchResults.getLimit());
    }

    @Test
    void shouldSearchForOnlyCharlesDickensUsingPhrase_NotAddingSurrounding() {

        SearchRequest search = MeiliQueryBuilder.fromIndex(indexName)
                .phrase("\"CHARLES DICKENS\"");
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        SearchResponse<Author> searchResults = client.search(search, Author.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false))
                .orElse(() -> null);

        Assertions.assertTrue(isSuccess.get(), "Search should execute");
        Assertions.assertEquals(1, searchResults.getEstimatedTotalHits(), "Must find only Dickens");
        Assertions.assertEquals(0, searchResults.getOffset());
        Assertions.assertEquals(20, searchResults.getLimit());
    }

    @Test
    void shouldSearchForOnlyEnglishAuthors_UsingFilter() {

        SearchRequest search = MeiliQueryBuilder.fromIndex(indexName)
                .filter("country = England");
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        SearchResponse<Author> searchResults = client.search(search, Author.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false))
                .orElse(() -> null);

        Assertions.assertTrue(isSuccess.get(), "Search should execute");
        Assertions.assertEquals(3, searchResults.getEstimatedTotalHits(), "Must find all English authors");
        Assertions.assertEquals(0, searchResults.getOffset());
        Assertions.assertEquals(20, searchResults.getLimit());
    }

    @Test
    void shouldSearch_UsingMultipleFilters() {

        SearchRequest search = MeiliQueryBuilder.fromIndex(indexName)
                .filter("country = England")
                .filter("uid = 1");
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        SearchResponse<Author> searchResults = client.search(search, Author.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false))
                .orElse(() -> null);

        Assertions.assertTrue(isSuccess.get(), "Search should execute");
        Assertions.assertEquals(1, searchResults.getEstimatedTotalHits(), "Must find only Jane Austen");
    }

    @Test
    void shouldSearch_UsingSingleFilter() {

        SearchRequest search = MeiliQueryBuilder.fromIndex(indexName)
                .filter("country = England AND uid = 1");
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        SearchResponse<Author> searchResults = client.search(search, Author.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false))
                .orElse(() -> null);

        Assertions.assertTrue(isSuccess.get(), "Search should execute");
        Assertions.assertEquals(1, searchResults.getEstimatedTotalHits(), "Must find only Jane Austen");
    }

    @Test
    void shouldSearch_UsingFilterAndQuery() {

        SearchRequest search = MeiliQueryBuilder.fromIndex(indexName)
                .filter("country = England AND uid = 1")
                .q("austen");
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        SearchResponse<Author> searchResults = client.search(search, Author.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false))
                .orElse(() -> null);

        Assertions.assertTrue(isSuccess.get(), "Search should execute");
        Assertions.assertEquals(1, searchResults.getEstimatedTotalHits(), "Must find only Jane Austen");
    }

    @Test
    void shouldSearch_UsingFilterAndQuery_noResult() {

        SearchRequest search = MeiliQueryBuilder.fromIndex(indexName)
                .filter("country = England AND uid = 1")
                .q("charles");
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        SearchResponse<Author> searchResults = client.search(search, Author.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false))
                .orElse(() -> null);

        Assertions.assertTrue(isSuccess.get(), "Search should execute");
        Assertions.assertEquals(0, searchResults.getEstimatedTotalHits(), "Must find none");
    }

    @Test
    void shouldSearch_GettingSubsetOfAttributes() {
        SearchRequest search = MeiliQueryBuilder.fromIndex(indexName)
                .filter("country = England")
                .retrieveAttributes(Arrays.asList("uid", "name"));//Otherwise AuthorNoCountry will not be deserialized

        AtomicBoolean isSuccess = new AtomicBoolean(false);
        client.search(search, Author.AuthorNoCountry.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false))
                .ifFailure(Throwable::printStackTrace);

        Assertions.assertTrue(isSuccess.get(), "Search should execute");
    }
    public static List<Author> authors() {
        Author austen = new Author();
        austen.setUid("1");
        austen.setName("Jane Austen");
        austen.setCountry("England");

        Author dickens = new Author();
        dickens.setUid("2");
        dickens.setName("Charles Dickens");
        dickens.setCountry("England");

        Author flaubert = new Author();
        flaubert.setUid("3");
        flaubert.setName("Gustave Flaubert");
        flaubert.setCountry("France");

        Author darwin = new Author();
        darwin.setUid("4");
        darwin.setName("Charles Darwin");
        darwin.setCountry("England");

        Author baudelaire = new Author();
        baudelaire.setUid("5");
        baudelaire.setName("Charles Baudelaire");
        baudelaire.setCountry("France");

        return List.of(austen, dickens, flaubert, darwin, baudelaire);
    }

}
