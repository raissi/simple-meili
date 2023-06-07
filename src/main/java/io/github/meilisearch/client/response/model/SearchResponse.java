package io.github.meilisearch.client.response.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SearchResponse<T> {

    private List<T> hits;
    private Map<String, Map<String, Integer>> facetDistribution;
    private Map<String, FacetStatMinMax> facetStats;

    private int offset;
    private int limit;
    private long estimatedTotalHits;
    private int processingTimeMs;
    private String query;

    @Data
    public static class FacetStatMinMax {
        private double min;
        private double max;
    }

}
