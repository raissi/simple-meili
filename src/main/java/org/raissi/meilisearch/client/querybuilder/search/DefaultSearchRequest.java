package org.raissi.meilisearch.client.querybuilder.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class DefaultSearchRequest extends BaseGet implements SearchRequest { //TODO add BasePaging

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String query;
    private int offset = 0;
    private int limit = 20;
    private List<String> filters;

    public DefaultSearchRequest(String index) {
        super("/indexes/" + index + "/search");
        filters = new ArrayList<>();
    }

    @Override
    public SearchRequest fetchOnly(List<String> fields) {
        return null;
    }

    @Override
    public SearchRequest startingAt(int offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public SearchRequest fetch(int limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public int limit() {
        return limit;
    }

    @Override
    public int offset() {
        return offset;
    }

    @Override
    public SearchRequest q(String q) {
        this.query = q;
        return this;
    }

    @Override
    public SearchRequest filter(String filter) {
        this.filters.add(filter);
        return this;
    }

    @Override
    public SearchRequest filters(Collection<String> filters) {
        Objects.requireNonNull(filters);
        this.filters.addAll(filters);
        return this;
    }

    @Override
    public SearchRequest facet(String facet) {
        return null;
    }

    @Override
    public SearchRequest facets(Collection<String> facets) {
        return null;
    }

    @Override
    public String query() {
        return query;
    }

    @Override
    public List<String> filters() {
        return filters;
    }

    @Override
    public String json() {
        Map<String, Object> body = new HashMap<>();
        Optional.ofNullable(query())
                .filter(s -> !s.isBlank())
                .ifPresent(s -> body.put("q", s));

        Optional.of(filters())
                .filter(l -> !l.isEmpty())
                .ifPresent(l -> body.put("filter", l));
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
