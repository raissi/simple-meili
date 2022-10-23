package org.raissi.meilisearch.client.querybuilder.insert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Optional;

public class DefaultOverrideDocuments<T> implements OverrideDocuments<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    protected final String index;
    protected final String completePath;
    private final List<T> documents;

    private String primaryKey;

    public DefaultOverrideDocuments(String index, List<T> documents) {
        this.index = index;
        this.documents = documents;
        this.completePath = "/indexes/"+index+"/"+"documents";
    }

    @Override
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
    public String index() {
        return index;
    }

    @Override
    public OverrideDocuments<T> withPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }

    @Override
    public Optional<String> primaryKey() {
        return Optional.ofNullable(primaryKey);
    }
}
