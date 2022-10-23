package org.raissi.meilisearch.client.response.exceptions;

public class NotFoundException extends MeiliSearchException {

    public NotFoundException(String path, String responseBody) {
        super("Resource: "+path+" was not found. Server responded with: "+responseBody);
    }

    public NotFoundException(String path, String message, String responseBody) {
        super("Resource :"+path+" was not found. Server responded with: "+message+". Complete response body: "+responseBody);
    }
}
