package org.raissi.meilisearch.client.querybuilder.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import org.raissi.meilisearch.client.querybuilder.MatchingStrategy;
import org.raissi.meilisearch.client.querybuilder.SortOrder;

import java.util.*;

import static java.util.Collections.singleton;
import static java.util.Optional.ofNullable;
import static org.raissi.meilisearch.client.querybuilder.DefaultQueryBuilder.SEARCH_PHRASE_DELIM;

public class DefaultSearchRequest extends BaseGet implements SearchRequest, SearchRequest.AroundPoint {

    public static final String STAR_WILDCARD = "*";
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private String query;
    private int offset = 0;
    private int limit = 20;
    private final List<String> filters;

    private final List<String> facets;

    private Set<String> attributesToRetrieve;
    private Set<String> attributesToCrop;

    private Integer cropLength;

    private String cropMarker = "…";

    private Set<String> attributesToHighlight;

    private String highlightPreTag = "<em>";

    private String highlightPostTag = "</em>";

    private boolean showMatchesPosition;

    private final List<String> sort;

    private MatchingStrategy matchingStrategy;

    private GeoPoint aroundPoint;

    public DefaultSearchRequest(String index) {
        super("/indexes/" + index + "/search");
        filters = new ArrayList<>();
        facets = new ArrayList<>();
        sort = new ArrayList<>();
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
    public SearchRequest phrase(String phrase) {
        this.query = surroundPhrase(phrase);
        return this;
    }

    @Override
    public SearchRequest filter(String filter) {
        Objects.requireNonNull(filter);
        if(!filter.isEmpty()) {
            this.filters.add(filter);
        }
        return this;
    }

    @Override
    public SearchRequest appendFilters(Collection<String> filters) {
        Objects.requireNonNull(filters);
        this.filters.addAll(filters);
        return this;
    }

    @Override
    public SearchRequest appendToQuery(String q) {
        this.query = Optional.ofNullable(this.query)
                .map(existingQuery -> existingQuery + " " + q)
                .orElse(q);
        return this;
    }

    @Override
    public SearchRequest appendPhraseToQuery(String q) {
        String phraseQ = surroundPhrase(q);
        this.query = Optional.ofNullable(this.query)
                .map(existingQuery -> existingQuery + " " + phraseQ)
                .orElse(phraseQ);
        return this;
    }

    @Override
    public SearchRequest clearFilters() {
        this.filters.clear();
        return this;
    }

    @Override
    public SearchRequest facet(String facet) {
        Objects.requireNonNull(facet);
        facets.add(facet);
        return this;
    }

    @Override
    public SearchRequest facets(Collection<String> facets) {
        Objects.requireNonNull(facets);
        this.facets.addAll(facets);
        return this;
    }

    @Override
    public SearchRequest retrieveAttributes(Collection<String> attributesToRetrieve) {
        Collection<String> attrsOrElseStar = ofNullable(attributesToRetrieve)
                .filter(c -> !c.isEmpty())
                .orElseGet(() -> singleton(STAR_WILDCARD));
        this.attributesToRetrieve = new HashSet<>(attrsOrElseStar);
        return this;
    }

    @Override
    public SearchRequest cropAllRetrievedAttributes() {
        this.attributesToCrop = Collections.singleton(STAR_WILDCARD);
        return this;
    }

    @Override
    public SearchRequest cropAttributes(Collection<String> attributesToCrop) {
        Collection<String> attrs = ofNullable(attributesToCrop).orElseGet(Collections::emptyList);
        this.attributesToCrop = new HashSet<>(attrs);
        return this;
    }

    @Override
    public SearchRequest cropLength(int cropLength) {
        if(cropLength > 0) {
            this.cropLength = cropLength;
        }
        return this;
    }

    @Override
    public SearchRequest markCropBoundariesWith(String cropMarker) {
        this.cropMarker = cropMarker;
        return this;
    }

    @Override
    public SearchRequest highlightAllRetrievedAttributes() {
        this.attributesToHighlight = Collections.singleton(STAR_WILDCARD);
        return this;
    }

    @Override
    public SearchRequest highlightAttributes(Collection<String> attributesToHighlight) {
        Collection<String> attrs = ofNullable(attributesToHighlight).orElseGet(Collections::emptyList);
        this.attributesToHighlight = new HashSet<>(attrs);
        return this;
    }

    @Override
    public SearchRequest highlightTags(String preTag, String postTag) {
        if(preTag != null && postTag != null && !preTag.isBlank() && !postTag.isBlank()) {
            this.highlightPreTag = preTag;
            this.highlightPostTag = postTag;
        }
        return this;
    }

    @Override
    public SearchRequest showMatchesPosition(boolean showMatchesPosition) {
        this.showMatchesPosition = showMatchesPosition;
        return this;
    }


    @Override
    public SearchRequest sortBy(String field, SortOrder sortOrder) {
        Objects.requireNonNull(field);
        this.sort.add(field + ":" + sortOrder.toString());
        return this;
    }

    @Override
    public SearchRequest sortAscBy(String field) {
        return sortBy(field, SortOrder.ASC);
    }

    @Override
    public SearchRequest sortDescBy(String field) {
        return sortBy(field, SortOrder.DESC);
    }

    @Override
    public SearchRequest sortByDistanceFromPoint(double lat, double lon, SortOrder sortOrder) {
        String geoPoint = "_geoPoint(" + lat + ", " + lon + ")";
        return sortBy(geoPoint, sortOrder);
    }


    @Override
    public SearchRequest matchDocumentsContainingAllQueryTerms() {
        return matchingStrategy(MatchingStrategy.ALL);
    }

    @Override
    public SearchRequest matchingStrategy(MatchingStrategy matchingStrategy) {
        this.matchingStrategy = matchingStrategy;
        return this;
    }

    @Override
    public AroundPoint aroundPoint(double lat, double lon) {
        this.aroundPoint = new GeoPoint(lat, lon);
        return this;
    }

    @Override
    public String json() {
        Map<String, Object> body = new HashMap<>();
        ofNullable(query)
                .filter(s -> !s.isBlank())
                .ifPresent(s -> body.put("q", s));

        Optional.of(this.filters)
                .filter(l -> !l.isEmpty())
                .ifPresent(l -> body.put("filter", l));

        Optional.of(this.facets)
                .filter(l -> !l.isEmpty())
                .ifPresent(l -> body.put("facets", l));

        Optional.ofNullable(this.attributesToRetrieve)
                .filter(l -> !l.isEmpty())
                .ifPresent(l -> body.put("attributesToRetrieve", l));

        Optional.ofNullable(this.attributesToCrop)
                .filter(l -> !l.isEmpty())
                .ifPresent(l -> {
                    body.put("attributesToCrop", l);
                    body.put("cropMarker", cropMarker);
                });
        ofNullable(cropLength)
                .ifPresent(length -> body.put("cropLength", length));

        Optional.ofNullable(this.attributesToHighlight)
                .filter(l -> !l.isEmpty())
                .ifPresent(l -> {
                    body.put("attributesToHighlight", l);
                    body.put("highlightPreTag", this.highlightPreTag);
                    body.put("highlightPostTag", this.highlightPostTag);
                });

        if(showMatchesPosition) {
            body.put("showMatchesPosition", true);
        }

        Optional.of(this.sort)
                .filter(l -> !l.isEmpty())
                .ifPresent(l -> body.put("sort", l));

        ofNullable(matchingStrategy)
                .ifPresent(strategy -> body.put("matchingStrategy", strategy.toString()));
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SearchRequest withinDistanceInMeters(int distanceInMeters) {
        String geoFilter = "_geoRadius(" + aroundPoint.getLat() + ", " + aroundPoint.getLon() + ", " + distanceInMeters + ")";
        return this.filter(geoFilter);
    }

    @Value
    static class GeoPoint {
        double lat;
        double lon;
    }

    private static String surroundPhrase(String phrase) {
        String surroundedPhrase;
        if (!phrase.startsWith(SEARCH_PHRASE_DELIM)) {
            surroundedPhrase = SEARCH_PHRASE_DELIM + phrase + SEARCH_PHRASE_DELIM;
        } else {
            surroundedPhrase = phrase;
        }
        return surroundedPhrase;
    }

}
