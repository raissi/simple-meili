package org.raissi.meilisearch.client.response.exceptions;

public class MeiliSearchException extends RuntimeException {
    public MeiliSearchException(String message) {
        super(message);
    }
}
