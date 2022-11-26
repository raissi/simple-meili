package io.github.meilisearch.client.querybuilder.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.meilisearch.client.response.model.GetResults;
import io.github.meilisearch.client.response.model.SearchResponse;

public class JacksonJsonReaderWriter implements JsonWriter, JsonReader {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public String json(Object body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T readValue(String content, Class<T> valueType) throws Exception {
        return objectMapper.readValue(content, valueType);
    }

    @Override
    public <T> GetResults<T> parseGetResults(String responseBody, Class<T> resultType) throws Exception {
        JavaType type = objectMapper.getTypeFactory().constructParametricType(GetResults.class, resultType);
        return objectMapper.readValue(responseBody, type);
    }

    @Override
    public <T> SearchResponse<T> parseSearchResults(String responseBody, Class<T> resultType) throws Exception {
        JavaType type = objectMapper.getTypeFactory().constructParametricType(SearchResponse.class, resultType);
        return objectMapper.readValue(responseBody, type);
    }
}
