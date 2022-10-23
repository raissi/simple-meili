package org.raissi.meilisearch.integration;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.*;
import org.raissi.meilisearch.client.MeiliClient;
import org.raissi.meilisearch.client.MeiliClientOkHttp;
import org.raissi.meilisearch.client.querybuilder.MeiliQueryBuilder;
import org.raissi.meilisearch.client.querybuilder.delete.DeleteDocumentsByIds;
import org.raissi.meilisearch.client.querybuilder.insert.UpsertDocuments;
import org.raissi.meilisearch.client.response.handler.CanBlockOnTask;
import org.raissi.meilisearch.client.response.model.MeiliAsyncWriteResponse;
import org.raissi.meilisearch.model.Author;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.raissi.meilisearch.integration.WriteDocumentsTest.authors;

public class DeleteDocumentsTest {

    static MeiliClient client;

    @BeforeAll
    public static void setUp() {
        OkHttpClient okHttpClient = new OkHttpClient();

        client = MeiliClientOkHttp.usingOkHttp(okHttpClient)
                .forHost("http://localhost:7700")
                .withSearchKey("masterKey");
    }

    @BeforeEach
    public void insertAuthors(TestInfo info) {
        String indexName = info.getDisplayName();
        List<Author> authors = authors();
        UpsertDocuments<Author> upsert = MeiliQueryBuilder.intoIndex(indexName).upsertDocuments(authors).withPrimaryKey("uid");
        client.upsert(upsert)
                .andThen(CanBlockOnTask::waitForCompletion);
    }

    @Test
    void shouldDeleteAllAndReturnEnqueued() {
        String indexName = "shouldDeleteAllAndReturnEnqueued";
        List<String> authorsIds = authors().stream().map(Author::getUid).collect(Collectors.toList());

        AtomicBoolean deleteSuccess = new AtomicBoolean(false);
        DeleteDocumentsByIds delete = MeiliQueryBuilder.fromIndex(indexName).delete(authorsIds);
        Optional<CanBlockOnTask> deleteByIds = client.deleteByIds(delete)
                .ifSuccess(s -> deleteSuccess.set(true))
                .ignoreErrors();
        Assertions.assertTrue(deleteSuccess.get());
        Assertions.assertEquals(MeiliAsyncWriteResponse.TASK_ENQUEUED, deleteByIds.map(MeiliAsyncWriteResponse::initialTaskStatus).orElse(null));
    }
}
