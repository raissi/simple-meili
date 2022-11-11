package org.raissi.meilisearch.integration;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.raissi.meilisearch.client.MeiliClient;
import org.raissi.meilisearch.client.MeiliClientOkHttp;
import org.raissi.meilisearch.client.querybuilder.MeiliQueryBuilder;
import org.raissi.meilisearch.client.querybuilder.insert.OverrideDocuments;
import org.raissi.meilisearch.client.querybuilder.insert.UpsertDocuments;
import org.raissi.meilisearch.client.querybuilder.search.GetDocuments;
import org.raissi.meilisearch.client.response.handler.CanBlockOnTask;
import org.raissi.meilisearch.client.response.model.MeiliAsyncWriteResponse;
import org.raissi.meilisearch.client.response.model.MeiliTask;
import org.raissi.meilisearch.client.response.model.GetResults;
import org.raissi.meilisearch.model.Author;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class WriteDocumentsITest extends BaseIntTest {

    @Test
    void shouldInsertAndReturnEnqueued() {
        AtomicBoolean upsertSuccess = new AtomicBoolean(false);
        UpsertDocuments<Author> upsert = MeiliQueryBuilder.intoIndex("writersInsert").upsertDocuments(authors()).withPrimaryKey("uid");
        Optional<CanBlockOnTask> upsertResponse = client.upsert(upsert)
                .ifSuccess(s -> upsertSuccess.set(true))
                .ignoreErrors();
        Assertions.assertTrue(upsertSuccess.get());
        Assertions.assertEquals(MeiliAsyncWriteResponse.TASK_ENQUEUED, upsertResponse.map(MeiliAsyncWriteResponse::initialTaskStatus).orElse(null));
    }

    @Test
    void shouldInsertAndBlockUntilSucceeded() {
        AtomicBoolean upsertSuccess = new AtomicBoolean(false);
        UpsertDocuments<Author> upsert = MeiliQueryBuilder.intoIndex("writersInsertBlock").upsertDocuments(authors()).withPrimaryKey("uid");
        Optional<MeiliTask> upsertResponse = client.upsert(upsert)
                .andThen(CanBlockOnTask::waitForCompletion)
                .ifSuccess(s -> upsertSuccess.set(true))
                .ignoreErrors();
        Assertions.assertTrue(upsertSuccess.get());
        Assertions.assertEquals(MeiliAsyncWriteResponse.TASK_SUCCEEDED, upsertResponse.map(MeiliTask::getStatus).orElse(null));
    }

    @Test
    void shouldInsertWithOverrideAndReturnEnqueued() {
        AtomicBoolean overrideSuccess = new AtomicBoolean(false);
        OverrideDocuments<Author> override = MeiliQueryBuilder.intoIndex("writersInsertOverride").overrideDocuments(authors()).withPrimaryKey("uid");
        Optional<CanBlockOnTask> overrideResponse = client.override(override)
                .ifSuccess(s -> overrideSuccess.set(true))
                .ignoreErrors();
        Assertions.assertTrue(overrideSuccess.get());
        Assertions.assertEquals(MeiliAsyncWriteResponse.TASK_ENQUEUED, overrideResponse.map(MeiliAsyncWriteResponse::initialTaskStatus).orElse(null));
    }

    @Test
    void shouldInsertWithOverrideAndBlockUntilSucceeded() {
        AtomicBoolean overrideSuccess = new AtomicBoolean(false);
        OverrideDocuments<Author> override = MeiliQueryBuilder.intoIndex("writersInsertOverrideBlock").overrideDocuments(authors()).withPrimaryKey("uid");
        Optional<MeiliTask> overrideResponse = client.override(override)
                .andThen(CanBlockOnTask::waitForCompletion)
                .ifSuccess(s -> overrideSuccess.set(true))
                .ignoreErrors();
        Assertions.assertTrue(overrideSuccess.get());
        Assertions.assertEquals(MeiliAsyncWriteResponse.TASK_SUCCEEDED, overrideResponse.map(MeiliTask::getStatus).orElse(null));
    }

    @Test
    void shouldOverrideAndBlockUntilSucceeded() {
        String indexName = "writersOverride";
        List<Author> authors = authors();
        UpsertDocuments<Author> upsert = MeiliQueryBuilder.intoIndex(indexName).upsertDocuments(authors).withPrimaryKey("uid");
        client.upsert(upsert)
                .andThen(CanBlockOnTask::waitForCompletion);

        List<Author.AuthorNoCountry> authorNoCountries = authors.stream().map(Author.AuthorNoCountry::new).collect(Collectors.toList());
        AtomicBoolean overrideSuccess = new AtomicBoolean(false);
        OverrideDocuments<Author.AuthorNoCountry> override = MeiliQueryBuilder.intoIndex(indexName).overrideDocuments(authorNoCountries).withPrimaryKey("uid");
        Optional<MeiliTask> overrideResponse = client.override(override)
                .andThen(CanBlockOnTask::waitForCompletion)
                .ifSuccess(s -> overrideSuccess.set(true))
                .ignoreErrors();
        Assertions.assertTrue(overrideSuccess.get());
        Assertions.assertEquals(MeiliAsyncWriteResponse.TASK_SUCCEEDED, overrideResponse.map(MeiliTask::getStatus).orElse(null));

        GetDocuments getAuthors = MeiliQueryBuilder.fromIndex(indexName).get();
        List<Author> authorsFromIndex = client.get(getAuthors, Author.class)
                .andThenTry(GetResults::getResults)
                .orElse(Collections::emptyList);

        Assertions.assertAll("Should have deleted country values",
                authorsFromIndex.stream().map(a -> () -> Assertions.assertNull(a.getCountry()))
        );
    }

    @Test
    void shouldTryOverrideAndBlockUntilFailed() {
        String indexName = "writersOverrideBadMapping";
        List<Author> authors = authors();
        UpsertDocuments<Author> upsert = MeiliQueryBuilder.intoIndex(indexName).upsertDocuments(authors).withPrimaryKey("uid");
        client.upsert(upsert)
                .andThen(CanBlockOnTask::waitForCompletion);

        List<Author.AuthorNoCountry> authorNoCountries = authors.stream().map(this::badMapper).collect(Collectors.toList());
        AtomicBoolean overrideSuccess = new AtomicBoolean(false);
        OverrideDocuments<Author.AuthorNoCountry> override = MeiliQueryBuilder.intoIndex(indexName).overrideDocuments(authorNoCountries).withPrimaryKey("uid");
        Optional<MeiliTask> overrideResponse = client.override(override)
                .andThen(CanBlockOnTask::waitForCompletion)
                .ifSuccess(s -> overrideSuccess.set(true))
                .ignoreErrors();
        Assertions.assertTrue(overrideSuccess.get());
        Assertions.assertEquals(MeiliAsyncWriteResponse.TASK_FAILED, overrideResponse.map(MeiliTask::getStatus).orElse(null));
        Assertions.assertNotNull(overrideResponse.flatMap(MeiliTask::getError).orElse(null));

    }

    private Author.AuthorNoCountry badMapper(Author a) {
        return new Author.AuthorNoCountry(a.getName(), a.getName());
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


        return List.of(austen, dickens, flaubert);
    }
}
