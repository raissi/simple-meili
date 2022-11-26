package io.github.meilisearch.client.response.handler;

import io.github.meilisearch.client.querybuilder.tasks.GetTask;
import io.github.meilisearch.client.response.model.MeiliTask;
import io.github.meilisearch.client.response.model.MeiliWriteResponse;
import io.github.meilisearch.client.MeiliClient;
import io.github.meilisearch.client.querybuilder.MeiliQueryBuilder;
import io.github.meilisearch.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This is a very simple Task waiting capable handler. Should be replaced with more robust one
 */
public class SimpleBlockingCapableTaskHandler implements CanBlockOnTask {

    private static final Logger logger = LoggerFactory.getLogger(SimpleBlockingCapableTaskHandler.class);

    private final MeiliWriteResponse writeResponse;
    private final MeiliClient meiliClient;

    public SimpleBlockingCapableTaskHandler(MeiliWriteResponse writeResponse, MeiliClient meiliClient) {
        this.writeResponse = writeResponse;
        this.meiliClient = meiliClient;
    }

    @Override
    public String taskId() {
        return String.valueOf(writeResponse.getTaskUid());
    }

    @Override
    public String index() {
        return writeResponse.index();
    }

    @Override
    public String initialTaskStatus() {
        return writeResponse.initialTaskStatus();
    }

    @Override
    public ZonedDateTime enqueuedAt() {
        return writeResponse.enqueuedAt();
    }

    @Override
    public String writeType() {
        return writeResponse.writeType();
    }

    @Override
    public Try<MeiliTask> waitForCompletion() {
        String status = writeResponse.initialTaskStatus();
        logger.debug("Starting to fetch status for task {} current status is {} (operation is: {})", taskId(), status, writeType());
        GetTask getTask = MeiliQueryBuilder.forTask(taskId());
        AtomicReference<Optional<Exception>> exceptionGettingStatus = new AtomicReference<>(Optional.empty());
        Try<MeiliTask> currentTask = Try.success(asMeiliTask());
        int calls = 0;
        while (!isComplete(status) && exceptionGettingStatus.get().isEmpty()) {
            currentTask = Try.of(() -> {
                Thread.sleep(20);
                return "OK to call again";
            }).andThen(s -> meiliClient.get(getTask))
            .ifFailure(e -> exceptionGettingStatus.set(Optional.of(e)));

            status = currentTask.andThenTry(MeiliTask::getStatus)
                    .orElse(() -> "serverError");
            calls++;
            logger.debug("On call #{} the returned status for task {} is {}", calls, taskId(), status);
        }

        return exceptionGettingStatus.get()
                .map(Try::<MeiliTask>failure)
                .orElse(currentTask);
    }
    private boolean isComplete(String status) {
        return TASK_SUCCEEDED.equals(status) || TASK_FAILED.equals(status);
    }

    private MeiliTask asMeiliTask() {
        MeiliTask task = new MeiliTask();
        task.setEnqueuedAt(enqueuedAt());
        task.setIndex(index());
        task.setStatus(initialTaskStatus());
        task.setOperationType(writeType());
        return task;
    }
}
