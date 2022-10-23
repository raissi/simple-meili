package org.raissi.meilisearch.client.querybuilder.insert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.raissi.meilisearch.client.querybuilder.HasBody;

import java.util.List;
import java.util.Optional;

public abstract class WriteWithBody<T> extends BaseWrite implements HasBody {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final List<T> documents;

    protected String primaryKey;

    public WriteWithBody(String index, List<T> documents) {
        super(index);
        this.documents = documents;
    }

    @Override
    public String json() {
        try {
            return objectMapper.writeValueAsString(documents);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<String> primaryKey() {
        return Optional.ofNullable(primaryKey);
    }
}
