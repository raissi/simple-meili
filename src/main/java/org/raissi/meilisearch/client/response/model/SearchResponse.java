package org.raissi.meilisearch.client.response.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SearchResponse<T> {

    private List<T> hits;
    private Map<String, Map<String, Integer>> facetDistribution;

    private int offset;
    private int limit;
    private long estimatedTotalHits;
    private int processingTimeMs;
    private String query;

}
