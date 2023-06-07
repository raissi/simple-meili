package io.github.meilisearch.client.querybuilder.delete;

import io.github.meilisearch.client.querybuilder.insert.BaseWrite;
import io.github.meilisearch.client.querybuilder.search.JsonWriter;

import java.util.*;

public class DefaultDeleteDocumentsByFilter extends BaseWrite implements DeleteDocumentsByFilter {

    private List<String> filters;
    public DefaultDeleteDocumentsByFilter(String index) {
        super("/indexes/"+index+"/"+"documents/delete", true);
    }

    @Override
    public String json(JsonWriter jsonWriter) {
        Map<String, Object> body = new HashMap<>();
        Optional.of(this.filters)
                .filter(l -> !l.isEmpty())
                .ifPresent(l -> body.put("filter", l));
        return jsonWriter.json(body);
    }

    @Override
    public DefaultDeleteDocumentsByFilter filter(String filter) {
        if(filters == null) {
            filters = new ArrayList<>();
        }
        filters.add(filter);
        return this;
    }
}
