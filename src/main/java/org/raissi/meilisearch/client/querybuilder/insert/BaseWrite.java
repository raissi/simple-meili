package org.raissi.meilisearch.client.querybuilder.insert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

public abstract class BaseWrite<T> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    protected final String completePath;
    private final List<T> documents;

    protected String primaryKey;

    public BaseWrite(String index, List<T> documents) {
        this.documents = documents;
        this.completePath = "/indexes/"+index+"/"+"documents";
    }

    public String json() {
        try {
            return objectMapper.writeValueAsString(documents);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String path() {
        return this.completePath;
    }

    public Optional<String> primaryKey() {
        return Optional.ofNullable(primaryKey);
    }

}
