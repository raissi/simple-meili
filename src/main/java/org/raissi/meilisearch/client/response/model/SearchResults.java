package org.raissi.meilisearch.client.response.model;

import java.util.Collections;
import java.util.List;

public class SearchResults<T> {

    private List<T> results;
    private int offset;
    private int limit;
    private int total;

    public static <T> SearchResults<T> empty() {
        SearchResults<T> emptyResults = new SearchResults<>();
        emptyResults.setResults(Collections.emptyList());
        return emptyResults;
    }
    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
