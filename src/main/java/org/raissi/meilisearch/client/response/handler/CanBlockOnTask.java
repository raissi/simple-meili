package org.raissi.meilisearch.client.response.handler;

import org.raissi.meilisearch.client.response.model.MeiliTask;
import org.raissi.meilisearch.control.Try;

import java.time.ZonedDateTime;

public interface CanBlockOnTask {

    String taskId();
    String index();
    String initialTaskStatus();
    ZonedDateTime enqueuedAt();
    String writeType();

    Try<MeiliTask> waitFroCompletion();

}
