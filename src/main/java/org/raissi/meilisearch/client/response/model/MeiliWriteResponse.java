package org.raissi.meilisearch.client.response.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class MeiliWriteResponse {


    private int taskUid;
    private String indexUid;
    private String status;
    @JsonProperty("type")
    private String writeType;
    private ZonedDateTime enqueuedAt;

    public int getTaskUid() {
        return taskUid;
    }

    public void setTaskUid(int taskUid) {
        this.taskUid = taskUid;
    }

    public String getIndexUid() {
        return indexUid;
    }

    public void setIndexUid(String indexUid) {
        this.indexUid = indexUid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWriteType() {
        return writeType;
    }

    public void setWriteType(String writeType) {
        this.writeType = writeType;
    }

    public ZonedDateTime getEnqueuedAt() {
        return enqueuedAt;
    }

    public void setEnqueuedAt(ZonedDateTime enqueuedAt) {
        this.enqueuedAt = enqueuedAt;
    }
}
