package org.raissi.meilisearch.client.querybuilder.insert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.raissi.meilisearch.client.querybuilder.HasBody;
import org.raissi.meilisearch.client.querybuilder.search.JsonWriter;

import java.util.List;
import java.util.Optional;

public abstract class WriteWithBody<T> extends BaseWrite implements HasBody {

    private final List<T> documents;
    protected String primaryKey;

    public WriteWithBody(String index, List<T> documents) {
        super(index);
        this.documents = documents;
    }

    public WriteWithBody(String indexOrPath, List<T> documents, boolean completePathComputed) {
        super(indexOrPath, completePathComputed);
        this.documents = documents;
    }

    @Override
    public String json(JsonWriter jsonWriter) {
        return jsonWriter.json(documents);
    }

    public Optional<String> primaryKey() {
        return Optional.ofNullable(primaryKey);
    }
}
