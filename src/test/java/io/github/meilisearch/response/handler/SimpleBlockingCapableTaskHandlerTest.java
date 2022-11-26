package io.github.meilisearch.response.handler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import io.github.meilisearch.client.MeiliClient;
import io.github.meilisearch.client.querybuilder.tasks.GetTask;
import io.github.meilisearch.client.response.handler.CanBlockOnTask;
import io.github.meilisearch.client.response.handler.SimpleBlockingCapableTaskHandler;
import io.github.meilisearch.client.response.model.MeiliTask;
import io.github.meilisearch.client.response.model.MeiliWriteResponse;
import io.github.meilisearch.control.Try;

import java.util.function.Function;

import static io.github.meilisearch.client.response.model.MeiliAsyncWriteResponse.TASK_ENQUEUED;

public class SimpleBlockingCapableTaskHandlerTest {

    @Test
    void onErrorCallingServerMustReturnFailure() {
        MeiliWriteResponse writeResponse = new MeiliWriteResponse();
        writeResponse.setStatus(TASK_ENQUEUED);
        writeResponse.setIndexUid("index");

        MeiliClient meiliClient = Mockito.mock(MeiliClient.class);

        String testErrorMessage = "some error just for test";
        Mockito.when(meiliClient.get(Mockito.any(GetTask.class))).thenThrow(new IllegalStateException(testErrorMessage));

        CanBlockOnTask taskHandler = new SimpleBlockingCapableTaskHandler(writeResponse, meiliClient);
        Try<MeiliTask> completeTask = taskHandler.waitForCompletion();
        ;

        Assertions.assertThatThrownBy(() -> { completeTask.orElseThrow(Function.identity()); })
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(testErrorMessage);
    }
}
