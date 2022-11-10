package org.raissi.meilisearch.client.querybuilder.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;

import java.util.*;

import static java.util.Collections.singleton;
import static java.util.Optional.ofNullable;

public class DefaultSearchRequest extends BaseGet implements SearchRequest, SearchRequest.AroundPoint { //TODO add BasePaging

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private String query;
    private int offset = 0;
    private int limit = 20;
    private final List<String> filters;

    private Set<String> attributesToRetrieve = singleton("*");
    private Set<String> attributesToCrop;

    private Integer cropLength;

    private String cropMarker = "…";

    private GeoPoint aroundPoint;

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
        Objects.requireNonNull(filter);
        if(!filter.isEmpty()) {
            this.filters.add(filter);
        }
        return this;
    }

    @Override
    public SearchRequest filters(Collection<String> filters) {
        Objects.requireNonNull(filters);
        this.filters.addAll(filters);
        return this;
    }

    @Override
    public SearchRequest clearFilters() {
        this.filters.clear();
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
    public SearchRequest retrieveAttributes(Collection<String> attributesToRetrieve) {
        Collection<String> attrsOrElseStar = ofNullable(attributesToRetrieve)
                .filter(c -> !c.isEmpty())
                .orElseGet(() -> singleton("*"));
        this.attributesToRetrieve = new HashSet<>(attrsOrElseStar);
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

        body.put("attributesToRetrieve", attributesToRetrieve);
        Optional.ofNullable(this.attributesToCrop)
                .filter(l -> !l.isEmpty())
                .ifPresent(l -> body.put("attributesToCrop", l));
        ofNullable(cropLength)
                .ifPresent(length -> body.put("cropLength", length));
        body.put("cropMarker", cropMarker);

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
}
