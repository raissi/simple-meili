package org.raissi.meilisearch.response.handler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.raissi.meilisearch.client.MeiliClient;
import org.raissi.meilisearch.client.querybuilder.tasks.GetTask;
import org.raissi.meilisearch.client.response.handler.CanBlockOnTask;
import org.raissi.meilisearch.client.response.handler.SimpleBlockingCapableTaskHandler;
import org.raissi.meilisearch.client.response.model.MeiliTask;
import org.raissi.meilisearch.client.response.model.MeiliWriteResponse;
import org.raissi.meilisearch.control.Try;

import java.util.function.Function;

import static org.raissi.meilisearch.client.response.model.MeiliAsyncWriteResponse.TASK_ENQUEUED;

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
