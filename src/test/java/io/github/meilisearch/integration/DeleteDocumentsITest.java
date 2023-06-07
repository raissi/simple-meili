package io.github.meilisearch.integration;

import io.github.meilisearch.client.MeiliClientOkHttp;
import io.github.meilisearch.client.querybuilder.MeiliQueryBuilder;
import io.github.meilisearch.client.querybuilder.delete.DeleteDocumentsByFilter;
import io.github.meilisearch.client.response.handler.CanBlockOnTask;
import io.github.meilisearch.client.response.model.MeiliTask;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.jupiter.api.*;
import io.github.meilisearch.client.querybuilder.delete.DeleteAllDocuments;
import io.github.meilisearch.client.querybuilder.delete.DeleteDocumentsByIds;
import io.github.meilisearch.client.querybuilder.delete.DeleteOneDocument;
import io.github.meilisearch.client.querybuilder.insert.UpsertDocuments;
import io.github.meilisearch.client.response.model.MeiliAsyncWriteResponse;
import io.github.meilisearch.model.Author;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.meilisearch.client.response.model.MeiliAsyncWriteResponse.TASK_ENQUEUED;
import static io.github.meilisearch.integration.WriteDocumentsITest.authors;


public class DeleteDocumentsITest extends BaseIntTest {



    @BeforeEach
    public void insertAuthors(TestInfo info) throws Exception {
        String indexName = info.getDisplayName();
        List<Author> authors = authors();
        UpsertDocuments upsert = MeiliQueryBuilder.intoIndex(indexName).upsertDocuments(authors).withPrimaryKey("uid");
        client.upsert(upsert)
                .andThen(CanBlockOnTask::waitForCompletion);

        // Define filterable attributes: for now simple-meili does not support admin operations
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(hostUrl+"/indexes/"+indexName+"/settings/filterable-attributes")
                .addHeader("Authorization", "Bearer masterKey")
                .put(RequestBody.create("[\"country\", \"uid\"]", MeiliClientOkHttp.JSON))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException();
            }
            Thread.sleep(300);
        }
    }

    @Test
    @DisplayName("shouldDeleteAllAndReturnEnqueued")
    void shouldDeleteAllAndReturnEnqueued(TestInfo info) {
        String indexName = info.getDisplayName();

        AtomicBoolean deleteSuccess = new AtomicBoolean(false);
        DeleteAllDocuments delete = MeiliQueryBuilder.fromIndex(indexName).deleteAll();
        Optional<CanBlockOnTask> deleteByIds = client.deleteAll(delete)
                .ifSuccess(s -> deleteSuccess.set(true))
                .ignoreErrors();
        Assertions.assertTrue(deleteSuccess.get());
        Assertions.assertEquals(TASK_ENQUEUED, deleteByIds.map(MeiliAsyncWriteResponse::initialTaskStatus).orElse(null));
    }

    @Test
    @DisplayName("shouldDeleteAllV2AndReturnEnqueued")
    void shouldDeleteAllV2AndReturnEnqueued(TestInfo info) {
        String indexName = info.getDisplayName();

        AtomicBoolean deleteSuccess = new AtomicBoolean(false);
        Optional<CanBlockOnTask> deleteByIds = client.deleteAll(indexName)
                .ifSuccess(s -> deleteSuccess.set(true))
                .ignoreErrors();
        Assertions.assertTrue(deleteSuccess.get());
        Assertions.assertEquals(TASK_ENQUEUED, deleteByIds.map(MeiliAsyncWriteResponse::initialTaskStatus).orElse(null));
    }

    @Test
    @DisplayName("shouldDeleteByIdsAndReturnEnqueued")
    void shouldDeleteByIdsAndReturnEnqueued(TestInfo info) throws Exception {
        String indexName = info.getDisplayName();
        List<String> authorsIds = authors().stream().map(Author::getUid).collect(Collectors.toList());

        AtomicReference<String> asyncReturn = new AtomicReference<>();
        DeleteDocumentsByIds delete = MeiliQueryBuilder.fromIndex(indexName).delete(authorsIds);
        MeiliTask deleteByIds = client.deleteByIds(delete)
                .ifSuccess(s -> asyncReturn.set(s.initialTaskStatus()))
                .andThen(CanBlockOnTask::waitForCompletion)
                .orElseThrow(Function.identity());
        Assertions.assertEquals(TASK_ENQUEUED, asyncReturn.get());
        Assertions.assertEquals(MeiliAsyncWriteResponse.TASK_SUCCEEDED, deleteByIds.getStatus());
        Assertions.assertEquals(authorsIds.size(), deleteByIds.getDetails().getDeletedDocuments());
    }

    @Test
    @DisplayName("shouldDeleteByFilterAndReturnEnqueued")
    void shouldDeleteByFilterAndReturnEnqueued(TestInfo info) throws Exception {
        String indexName = info.getDisplayName();
        List<String> authorsFromEngland = authors().stream()
                .map(Author::getCountry)
                .filter(s -> s.equalsIgnoreCase("England"))
                .collect(Collectors.toList());

        AtomicReference<String> asyncReturn = new AtomicReference<>();
        DeleteDocumentsByFilter delete = MeiliQueryBuilder.fromIndex(indexName).deleteByFilter("country = England");
        MeiliTask deleteByIds = client.deleteByFilter(delete)
                .ifSuccess(s -> asyncReturn.set(s.initialTaskStatus()))
                .andThen(CanBlockOnTask::waitForCompletion)
                .orElseThrow(Function.identity());
        Assertions.assertEquals(TASK_ENQUEUED, asyncReturn.get());
        Assertions.assertEquals(MeiliAsyncWriteResponse.TASK_SUCCEEDED, deleteByIds.getStatus());
        Assertions.assertEquals(authorsFromEngland.size(), deleteByIds.getDetails().getDeletedDocuments());
    }

    @Test
    @DisplayName("shouldDeleteByMultipleFiltersAndReturnEnqueued")
    void shouldDeleteByMultipleFiltersAndReturnEnqueued(TestInfo info) throws Exception {
        String indexName = info.getDisplayName();
        List<Author> authorsFromEngland = authors().stream()
                .filter(s -> s.getCountry().equalsIgnoreCase("England"))
                .collect(Collectors.toList());

        AtomicReference<String> asyncReturn = new AtomicReference<>();
        DeleteDocumentsByFilter delete = MeiliQueryBuilder.fromIndex(indexName)
                .deleteByFilter("country = England")
                .filter("uid = " + authorsFromEngland.get(0).getUid());
        MeiliTask deleteByIds = client.deleteByFilter(delete)
                .ifSuccess(s -> asyncReturn.set(s.initialTaskStatus()))
                .andThen(CanBlockOnTask::waitForCompletion)
                .orElseThrow(Function.identity());
        Assertions.assertEquals(TASK_ENQUEUED, asyncReturn.get());
        Assertions.assertEquals(MeiliAsyncWriteResponse.TASK_SUCCEEDED, deleteByIds.getStatus());
        Assertions.assertEquals(1, deleteByIds.getDetails().getDeletedDocuments());
    }

    @Test
    @DisplayName("shouldDeleteByIdsV2AndReturnEnqueued")
    void shouldDeleteByIdsV2AndReturnEnqueued(TestInfo info) {
        String indexName = info.getDisplayName();
        List<String> authorsIds = authors().stream().map(Author::getUid).collect(Collectors.toList());

        AtomicBoolean deleteSuccess = new AtomicBoolean(false);
        Optional<CanBlockOnTask> deleteByIds = client.deleteByIds(indexName, authorsIds)
                .ifSuccess(s -> deleteSuccess.set(true))
                .ignoreErrors();
        Assertions.assertTrue(deleteSuccess.get());
        Assertions.assertEquals(TASK_ENQUEUED, deleteByIds.map(MeiliAsyncWriteResponse::initialTaskStatus).orElse(null));
    }

    @Test
    @DisplayName("shouldDeleteAndBlockUntilSucceeded")
    void shouldDeleteAndBlockUntilSucceeded(TestInfo info) {
        String indexName = info.getDisplayName();
        List<String> authorsIds = authors().stream().map(Author::getUid).collect(Collectors.toList());

        AtomicBoolean deleteSuccess = new AtomicBoolean(false);
        DeleteDocumentsByIds delete = MeiliQueryBuilder.fromIndex(indexName).delete(authorsIds);
        Optional<MeiliTask> deleteByIds = client.deleteByIds(delete)
                .andThen(CanBlockOnTask::waitForCompletion)
                .ifSuccess(s -> deleteSuccess.set(true))
                .ignoreErrors();
        Assertions.assertTrue(deleteSuccess.get());
        Assertions.assertEquals(MeiliAsyncWriteResponse.TASK_SUCCEEDED, deleteByIds.map(MeiliTask::getStatus).orElse(null));
    }

    @Test
    @DisplayName("shouldDeleteByIdAndBlockUntilSucceeded")
    void shouldDeleteByIdAndBlockUntilSucceeded(TestInfo info) {
        String indexName = info.getDisplayName();
        String authorId = authors().get(0).getUid();

        AtomicBoolean deleteSuccess = new AtomicBoolean(false);
        DeleteOneDocument delete = MeiliQueryBuilder.fromIndex(indexName).delete(authorId);
        Optional<MeiliTask> deleteByIds = client.deleteOne(delete)
                .andThen(CanBlockOnTask::waitForCompletion)
                .ifSuccess(s -> deleteSuccess.set(true))
                .ignoreErrors();
        Assertions.assertTrue(deleteSuccess.get());
        Assertions.assertEquals(MeiliAsyncWriteResponse.TASK_SUCCEEDED, deleteByIds.map(MeiliTask::getStatus).orElse(null));
    }

    @Test
    @DisplayName("shouldDeleteV2ByIdAndBlockUntilSucceeded")
    void shouldDeleteV2ByIdAndBlockUntilSucceeded(TestInfo info) {
        String indexName = info.getDisplayName();
        String authorId = authors().get(0).getUid();

        AtomicBoolean deleteSuccess = new AtomicBoolean(false);
        MeiliTask deleteByIds = client.deleteOne(indexName, authorId)
                .andThen(CanBlockOnTask::waitForCompletion)
                .ifSuccess(s -> deleteSuccess.set(true))
                .ignoreErrors()
                .orElseThrow();
        Assertions.assertTrue(deleteSuccess.get());
        Assertions.assertEquals(MeiliAsyncWriteResponse.TASK_SUCCEEDED, deleteByIds.getStatus());
        Assertions.assertEquals(1, deleteByIds.getDetails().getDeletedDocuments());
    }
}
