package io.github.meilisearch.client.response.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MeiliError {

    public static final String DOCUMENT_NOT_FOUND_ERROR = "document_not_found";

    private String message;
    private String code;

    @JsonProperty("type")
    private String errorType;
    private String link;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
