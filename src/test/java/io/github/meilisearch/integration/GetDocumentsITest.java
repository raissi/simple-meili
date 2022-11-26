package io.github.meilisearch.integration;

import io.github.meilisearch.client.querybuilder.MeiliQueryBuilder;
import io.github.meilisearch.client.querybuilder.search.GetDocumentIgnoreNotFound;
import io.github.meilisearch.client.querybuilder.search.GetDocuments;
import io.github.meilisearch.model.Movie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.meilisearch.client.querybuilder.search.GetDocument;
import io.github.meilisearch.client.response.model.GetResults;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GetDocumentsITest extends BaseIntTest{

    @Test
    public void shouldFailForGetListOfMoviesWhenIndexNonValid() {
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        GetDocuments getMovies = MeiliQueryBuilder.fromIndex("moviesInvalidIndex").get();
        client.get(getMovies, Movie.class)
                .andThenTry(GetResults::getResults)
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
        GetResults<Movie> getResults = client.get(getMovies, Movie.class)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false))
                .orElse(() -> null);

        Assertions.assertTrue(isSuccess.get(), "Get documents should execute");
        Assertions.assertTrue(getResults.getTotal() > 0);
        Assertions.assertEquals(0, getResults.getOffset());
        Assertions.assertEquals(20, getResults.getLimit());
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
                .andThenTry(GetResults::getResults)
                .ifSuccess(s -> isSuccess.set(true))
                .ifFailure(s -> isSuccess.set(false));

        Assertions.assertFalse(isSuccess.get(), "When index not found, always return error");
    }

    @Test
    public void getListOfMoviesLimited() {
        GetDocuments getMovies = MeiliQueryBuilder.fromIndex("movies").get().fetch(3);
        List<Movie> movies = client.get(getMovies, Movie.class)
                .andThenTry(GetResults::getResults)
                .orElse(Collections::emptyList);

        Assertions.assertEquals(3, movies.size(), "Limit was 3");
    }

    @Test
    public void getListOfMoviesWithSpecificFields() {
        GetDocuments getMovies = MeiliQueryBuilder.fromIndex("movies").get()
                            .fetch(5).fetchOnly(Arrays.asList("id", "title"));
        AtomicBoolean isSuccess = new AtomicBoolean(false);

        List<Movie> movies = client.get(getMovies, Movie.class)
                .andThenTry(GetResults::getResults)
                .ifSuccess(s -> isSuccess.set(true))
                .orElse(Collections::emptyList);

        Assertions.assertTrue(isSuccess.get(), "Must succeed");

        Assertions.assertAll("Should not return overview field",
            movies.stream().map(movie -> () -> Assertions.assertNull(movie.getOverview()))
        );
    }

}
