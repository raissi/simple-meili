package org.raissi.meilisearch.integration;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.raissi.meilisearch.client.MeiliClient;
import org.raissi.meilisearch.client.MeiliClientOkHttp;
import org.raissi.meilisearch.client.querybuilder.MeiliQueryBuilder;
import org.raissi.meilisearch.client.querybuilder.search.GetDocument;
import org.raissi.meilisearch.client.querybuilder.search.GetDocumentIgnoreNotFound;
import org.raissi.meilisearch.client.querybuilder.search.GetDocuments;
import org.raissi.meilisearch.client.response.SearchResponse;
import org.raissi.meilisearch.client.response.model.SearchResults;
import org.raissi.meilisearch.model.Movie;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GetDocumentsTest {

    static MeiliClient client;

    @BeforeAll
    public static void setUp() {
        OkHttpClient okHttpClient = new OkHttpClient();

        client = MeiliClientOkHttp.usingOkHttp(okHttpClient)
                .forHost("http://localhost:7700")
                .withSearchKey("masterKey");
    }

    @Test
    public void shouldFailForGetListOfMoviesWhenIndexNonValid() {
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        GetDocuments getMovies = MeiliQueryBuilder.fromIndex("moviesInvalidIndex").get();
        client.get(getMovies, Movie.class)
                .andThenTry(SearchResponse::list)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false));

        Assertions.assertFalse(isSuccess.get(), "Expect error for invalid index");
    }

    @Test
    public void shouldFailForGetMovieWhenIndexNonValid() {
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        GetDocument getMovies = MeiliQueryBuilder.fromIndex("moviesInvalidIndex").get("2");
        client.get(getMovies, Movie.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false));

        Assertions.assertFalse(isSuccess.get(), "Expect error for invalid index");
    }

    @Test
    public void shouldGetListOfMovies() {
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        GetDocuments getMovies = MeiliQueryBuilder.fromIndex("movies").get();
        SearchResults<Movie> searchResults = client.get(getMovies, Movie.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false))
                .orElse(() -> null)
                .searchResults();

        Assertions.assertTrue(isSuccess.get(), "Get documents should execute");
        Assertions.assertTrue(searchResults.getTotal() > 0);
        Assertions.assertEquals(0, searchResults.getOffset());
        Assertions.assertEquals(20, searchResults.getLimit());
    }

    @Test
    public void shouldGetMovie() {
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        GetDocument getMovies = MeiliQueryBuilder.fromIndex("movies").get("2");
        client.get(getMovies, Movie.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false));

        Assertions.assertTrue(isSuccess.get(), "Get document #2 should execute");
    }
    @Test
    public void getNotFoundMovieShouldFailByDefault() {
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        GetDocument getMovies = MeiliQueryBuilder.fromIndex("movies").get("2aaaaa");
        client.get(getMovies, Movie.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false));

        Assertions.assertFalse(isSuccess.get(), "Get document #2aaaaa should fail");
    }

    @Test
    public void getNotFoundMovieErrorCanBeDisabled() {
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        GetDocumentIgnoreNotFound getMovies = MeiliQueryBuilder.fromIndex("movies").get("2aaaaa").notFoundAsEmpty();
        client.get(getMovies, Movie.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false));

        Assertions.assertTrue(isSuccess.get(), "When notFoundAsEmpty is enabled, no error for not found docs");
    }

    @Test
    public void getNotFoundMovieErrorCanBeDisabledButNotNotFoundIndex() {
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        GetDocumentIgnoreNotFound getMovie = MeiliQueryBuilder.fromIndex("moviesInvalidIndex").get("2aaaaa").notFoundAsEmpty();
        client.get(getMovie, Movie.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false));

        Assertions.assertFalse(isSuccess.get(), "When index not found, always return error");
    }

    @Test
    public void getNotFoundMovieErrorCanBeDisabledButNotNotFoundIndex_ForList() {
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        GetDocuments getMovies = MeiliQueryBuilder.fromIndex("moviesInvalidIndex").get();
        client.get(getMovies, Movie.class)
                .andThenTry(SearchResponse::list)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false));

        Assertions.assertFalse(isSuccess.get(), "When index not found, always return error");
    }

    @Test
    public void getListOfMoviesLimited() {
        GetDocuments getMovies = MeiliQueryBuilder.fromIndex("movies").get().fetch(3);
        List<Movie> movies = client.get(getMovies, Movie.class)
                .andThenTry(SearchResponse::list)
                .orElse(Collections::emptyList);

        Assertions.assertEquals(3, movies.size(), "Limit was 3");
    }

    @Test
    public void getListOfMoviesWithSpecificFields() {
        GetDocuments getMovies = MeiliQueryBuilder.fromIndex("movies").get()
                            .fetch(5).fetchOnly(Arrays.asList("id", "title"));
        List<Movie> movies = client.get(getMovies, Movie.class)
                .andThenTry(SearchResponse::list)
                .orElse(Collections::emptyList);

        Assertions.assertAll("Should return address of Oracle's headquarter",
            movies.stream().map(movie -> () -> Assertions.assertNull(movie.getOverview()))
        );
    }

}
