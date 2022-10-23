package org.raissi.meilisearch.client.response.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class MeiliWriteResponse implements MeiliAsyncWriteResponse{


    private int taskUid;
    private String indexUid;
    private String status;
    @JsonProperty("type")
    private String writeType;
    private ZonedDateTime enqueuedAt;

    public int getTaskUid() {
        return taskUid;
    }

    @Override
    public String taskId() {
        return String.valueOf(taskUid);
    }

    public void setTaskUid(int taskUid) {
        this.taskUid = taskUid;
    }

    @Override
    public String index() {
        return indexUid;
    }

    public void setIndexUid(String indexUid) {
        this.indexUid = indexUid;
    }

    @Override
    public String initialTaskStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String writeType() {
        return writeType;
    }

    public void setWriteType(String writeType) {
        this.writeType = writeType;
    }

    @Override
    public ZonedDateTime enqueuedAt() {
        return enqueuedAt;
    }

    public void setEnqueuedAt(ZonedDateTime enqueuedAt) {
        this.enqueuedAt = enqueuedAt;
    }
}
