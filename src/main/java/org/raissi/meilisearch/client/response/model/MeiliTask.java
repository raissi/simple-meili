package org.raissi.meilisearch.client.response.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.raissi.meilisearch.client.response.exceptions.MeiliSearchException;
import org.raissi.meilisearch.control.Try;

import java.time.ZonedDateTime;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
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
    private MeiliError error;
    private MeiliTaskDetails details;

    public Optional<MeiliError> getError() {
        return Optional.ofNullable(error);
    }

    public Try<MeiliTask> extractError() {
        return getError().map(e -> Try.<MeiliTask>failure(new MeiliSearchException(e.getMessage())))//TODO exception should contain all details
                .orElseGet(() -> Try.success(this));

    }
}
