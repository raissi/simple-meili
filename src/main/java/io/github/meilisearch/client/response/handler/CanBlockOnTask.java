package io.github.meilisearch.client.response.handler;

import io.github.meilisearch.client.response.model.MeiliTask;
import io.github.meilisearch.client.response.model.MeiliAsyncWriteResponse;
import io.github.meilisearch.control.Try;

public interface CanBlockOnTask extends MeiliAsyncWriteResponse {
    Try<MeiliTask> waitForCompletion();

}
