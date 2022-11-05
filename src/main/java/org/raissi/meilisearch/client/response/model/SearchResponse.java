package org.raissi.meilisearch.client.response.model;

import lombok.Data;

import java.util.List;

@Data
public class SearchResponse<T> {

    private List<T> hits;
    private int offset;
    private int limit;
    private long estimatedTotalHits;
    private int processingTimeMs;
    private String query;

}
