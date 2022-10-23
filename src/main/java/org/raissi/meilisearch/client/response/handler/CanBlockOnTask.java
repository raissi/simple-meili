package org.raissi.meilisearch.client.response.handler;

import org.raissi.meilisearch.client.response.model.MeiliAsyncWriteResponse;
import org.raissi.meilisearch.client.response.model.MeiliTask;
import org.raissi.meilisearch.control.Try;

public interface CanBlockOnTask extends MeiliAsyncWriteResponse {
    Try<MeiliTask> waitForCompletion();

}
