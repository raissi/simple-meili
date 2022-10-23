package org.raissi.meilisearch;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.raissi.meilisearch.client.MeiliClient;
import org.raissi.meilisearch.client.MeiliClientOkHttp;
import org.raissi.meilisearch.client.querybuilder.MeiliQueryBuilder;
import org.raissi.meilisearch.client.querybuilder.insert.OverrideDocuments;
import org.raissi.meilisearch.client.querybuilder.search.GetDocument;
import org.raissi.meilisearch.client.querybuilder.search.GetDocumentIgnoreNotFound;
import org.raissi.meilisearch.client.querybuilder.search.GetDocuments;
import org.raissi.meilisearch.client.response.handler.CanBlockOnTask;
import org.raissi.meilisearch.model.Movie;

import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {

        OkHttpClient okHttpClient = new OkHttpClient();

        MeiliClient client = MeiliClientOkHttp.usingOkHttp(okHttpClient)
                .forHost("http://localhost:7700")
                .withSearchKey("masterKey");

        GetDocuments getMovies = MeiliQueryBuilder.fromIndex("movies").get().fetch(200);
        List<Movie> movies = client.get(getMovies, Movie.class)
                .ifFailure(Throwable::printStackTrace)
                .orElse(() -> null)
                .list();

        GetDocument getMovie = MeiliQueryBuilder.fromIndex("movies").get("2aaaaa");
        client.get(getMovie, Movie.class)
                .ifSuccess(System.out::println)
                .ifFailure(Throwable::printStackTrace);

        GetDocumentIgnoreNotFound getMovieNotFound = MeiliQueryBuilder.fromIndex("movieszzzz").get("2zzzzz").notFoundAsEmpty();
        client.get(getMovieNotFound, Movie.class)
                .ifSuccess(System.out::println)
                .ifFailure(Throwable::printStackTrace);

        OverrideDocuments<Movie> overrideMovies = MeiliQueryBuilder.intoIndex("movies3").overrideDocuments(movies).withPrimaryKey("id");
        client.override(overrideMovies)
                .andThen(CanBlockOnTask::waitForCompletion)
                .ifFailure(Throwable::printStackTrace)
                .ifSuccess(lastStatus -> System.out.println(lastStatus.getStatus()));



    }


}
