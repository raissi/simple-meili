package org.raissi.meilisearch.client.response.model;

import java.time.ZonedDateTime;

public interface MeiliAsyncWriteResponse {

    String TASK_ENQUEUED = "enqueued";
    String TASK_SUCCEEDED = "succeeded";

    String TASK_FAILED = "failed";
    String taskId();
    String index();
    String initialTaskStatus();
    ZonedDateTime enqueuedAt();
    String writeType();
}
