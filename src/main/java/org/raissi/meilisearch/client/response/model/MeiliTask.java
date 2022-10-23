package org.raissi.meilisearch.client.response.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MeiliTask {
    /*
    TODO
  "details":{
    "rankingRules":[
      "typo",
      "ranking:desc",
      "words",
      "proximity",
      "attribute",
      "exactness"
    ]
  }
     */

    private String uid;
    @JsonProperty("indexUid")
    private String index;
    private String status;
    @JsonProperty("type")
    private String operationType;
    private ZonedDateTime enqueuedAt;
    private ZonedDateTime startedAt;
    private ZonedDateTime finishedAt;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public ZonedDateTime getEnqueuedAt() {
        return enqueuedAt;
    }

    public void setEnqueuedAt(ZonedDateTime enqueuedAt) {
        this.enqueuedAt = enqueuedAt;
    }

    public ZonedDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(ZonedDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public ZonedDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(ZonedDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }
}
