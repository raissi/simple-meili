package org.raissi.meilisearch.integration;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.raissi.meilisearch.client.MeiliClient;
import org.raissi.meilisearch.client.MeiliClientOkHttp;
import org.raissi.meilisearch.client.querybuilder.MeiliQueryBuilder;
import org.raissi.meilisearch.client.querybuilder.insert.UpsertDocuments;
import org.raissi.meilisearch.client.querybuilder.search.SearchRequest;
import org.raissi.meilisearch.client.response.handler.CanBlockOnTask;
import org.raissi.meilisearch.client.response.model.SearchResponse;
import org.raissi.meilisearch.control.Try;
import org.raissi.meilisearch.model.Author;
import org.raissi.meilisearch.model.Author.AuthorFormatted;
import org.raissi.meilisearch.model.Author.AuthorWithPositions;
import org.raissi.meilisearch.model.MatchPosition;

import java.util.*;
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

        Assertions.assertThat(isSuccess).as("Search should execute").isTrue();
        Assertions.assertThat(searchResults.getEstimatedTotalHits()).as("Must find the only Jane").isEqualTo(1);
        Assertions.assertThat(searchResults.getOffset()).isEqualTo(0);
        Assertions.assertThat(searchResults.getLimit()).isEqualTo(20);
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

        Assertions.assertThat(isSuccess).as("Search should execute").isTrue();
        Assertions.assertThat(searchResults.getEstimatedTotalHits()).as("Must find darwin and dickens and baudelaire").isEqualTo(3);
        Assertions.assertThat(searchResults.getOffset()).isEqualTo(0);
        Assertions.assertThat(searchResults.getLimit()).isEqualTo(20);
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

        Assertions.assertThat(isSuccess).as("Search should execute").isTrue();
        Assertions.assertThat(searchResults.getEstimatedTotalHits()).as("Must find all charles").isEqualTo(3);
        Assertions.assertThat(searchResults.getOffset()).isEqualTo(0);
        Assertions.assertThat(searchResults.getLimit()).isEqualTo(20);
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

        Assertions.assertThat(isSuccess).as("Search should execute").isTrue();
        Assertions.assertThat(searchResults.getEstimatedTotalHits()).as("Must find only Dickens").isEqualTo(1);
        Assertions.assertThat(searchResults.getOffset()).isEqualTo(0);
        Assertions.assertThat(searchResults.getLimit()).isEqualTo(20);
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

        Assertions.assertThat(isSuccess).as("Search should execute").isTrue();
        Assertions.assertThat(searchResults.getEstimatedTotalHits()).as("Must find only Dickens").isEqualTo(1);
        Assertions.assertThat(searchResults.getOffset()).isEqualTo(0);
        Assertions.assertThat(searchResults.getLimit()).isEqualTo(20);
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

        Assertions.assertThat(isSuccess).as("Search should execute").isTrue();
        Assertions.assertThat(searchResults.getEstimatedTotalHits()).as("Must find only Dickens").isEqualTo(1);
        Assertions.assertThat(searchResults.getOffset()).isEqualTo(0);
        Assertions.assertThat(searchResults.getLimit()).isEqualTo(20);
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

        Assertions.assertThat(isSuccess).as("Search should execute").isTrue();
        Assertions.assertThat(searchResults.getEstimatedTotalHits()).as("Must find all English authors").isEqualTo(3);
        Assertions.assertThat(searchResults.getOffset()).isEqualTo(0);
        Assertions.assertThat(searchResults.getLimit()).isEqualTo(20);
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

        Assertions.assertThat(isSuccess).as("Search should execute").isTrue();
        Assertions.assertThat(searchResults.getEstimatedTotalHits()).as("Must find only Jane Austen").isEqualTo(1);
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

        Assertions.assertThat(isSuccess).as("Search should execute").isTrue();
        Assertions.assertThat(searchResults.getEstimatedTotalHits()).as("Must find only Jane Austen").isEqualTo(1);
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

        Assertions.assertThat(isSuccess).as("Search should execute").isTrue();
        Assertions.assertThat(searchResults.getEstimatedTotalHits()).as("Must find only Jane Austen").isEqualTo(1);
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

        Assertions.assertThat(isSuccess).as("Search should execute").isTrue();
        Assertions.assertThat(searchResults.getEstimatedTotalHits()).as("Must find none").isEqualTo(0);
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

        Assertions.assertThat(isSuccess).isTrue();
    }

    @Test
    void shouldCropAttributes() {
        SearchRequest search = MeiliQueryBuilder.fromIndex(indexName)
                .q("british")
                .cropAttributes(Collections.singleton("bio"))
                .cropLength(6)
                .markCropBoundariesWith("...");
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        Optional<Author> formattedFirstAuthor = client.search(search, AuthorFormatted.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false))
                .ifFailure(Throwable::printStackTrace)
                .andThenTry(SearchResponse::getHits)
                .orElse(Collections::emptyList)
                .stream()
                .map(AuthorFormatted::getFormatted)
                .findFirst();

        Assertions.assertThat(isSuccess).isTrue();
        Assertions.assertThat(formattedFirstAuthor).as("Must contain formatted").isPresent();
        Assertions.assertThat(formattedFirstAuthor)
                .hasValueSatisfying(a -> { Assertions.assertThat(a.getBio()).startsWith("...").endsWith("..."); });
    }

    @Test
    void shouldHighlightAttributes() {
        SearchRequest search = MeiliQueryBuilder.fromIndex(indexName)
                .q("British")
                .highlightAttributes(Collections.singleton("bio"))
                .highlightTags("startWithTag-", "-endWithTag");
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        Optional<Author> formattedFirstAuthor = client.search(search, AuthorFormatted.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false))
                .ifFailure(Throwable::printStackTrace)
                .andThenTry(SearchResponse::getHits)
                .orElse(Collections::emptyList)
                .stream()
                .map(AuthorFormatted::getFormatted)
                .findFirst();

        Assertions.assertThat(isSuccess).as("Search should execute").isTrue();
        Assertions.assertThat(formattedFirstAuthor)
                .hasValueSatisfying(a -> {
                    Assertions.assertThat(a.getBio()).containsIgnoringCase("startWithTag-British-endWithTag"); });
    }

    @Test
    void shouldGetMatchesPosition() {
        SearchRequest search = MeiliQueryBuilder.fromIndex(indexName)
                .q("novel")
                .filter("uid = 1")
                .showMatchesPosition(true);
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        Optional<Map<String, List<MatchPosition>>> matchesPosition = client.search(search, AuthorWithPositions.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false))
                .ifFailure(Throwable::printStackTrace)
                .andThenTry(SearchResponse::getHits)
                .orElse(Collections::emptyList)
                .stream()
                .map(AuthorWithPositions::getMatchesPosition)
                .findFirst();

        Assertions.assertThat(isSuccess).as("Search should execute").isTrue();
        Assertions.assertThat(matchesPosition)
                .hasValueSatisfying(matchPositions -> {
                    Assertions.assertThat(matchPositions.get("bio")).as("novel appears twice in bio of Austen").hasSize(2);
                });
    }


    @Test
    void shouldSearchWithFacets() {
        SearchRequest search = MeiliQueryBuilder.fromIndex(indexName)
                .q("charles")
                .facet("country");
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        Map<String, Map<String, Integer>> distribution = client.search(search, AuthorWithPositions.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false))
                .ifFailure(Throwable::printStackTrace)
                .andThenTry(SearchResponse::getFacetDistribution)
                .orElse(Collections::emptyMap);
        Assertions.assertThat(isSuccess).as("Search should execute").isTrue();
        Map<String, Integer> countryDist = distribution.get("country");
        Assertions.assertThat(countryDist).as("Both France and England have authors named Charles").hasSize(2);
        Integer englandCount = countryDist.get("England");
        Assertions.assertThat(englandCount).as("England has two authors named Charles").isEqualTo(2);
    }


    public static List<Author> authors() {
        Author austen = new Author();
        austen.setUid("1");
        austen.setName("Jane Austen");
        austen.setCountry("England");
        austen.setBio("Jane Austen was an English novelist known primarily " +
                "for her six major novels, which interpret, critique, " +
                "and comment upon the British landed gentry at the end " +
                "of the 18th century. Austen's plots often explore the dependence " +
                "of women on marriage in the pursuit of favourable social standing and economic security.");

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
