package org.raissi.meilisearch.client.querybuilder.search;

import org.raissi.meilisearch.client.querybuilder.HasBody;

import java.util.Collection;

public interface SearchRequest extends PagingRequest<SearchRequest>, HasBody {
    //TODO add method to append query terms e.g: \"african american\" horror

    SearchRequest q(String q);

    SearchRequest filter(String filter);
    SearchRequest filters(Collection<String> filters);

    SearchRequest clearFilters();

    SearchRequest facet(String facet);
    SearchRequest facets(Collection<String> facets);

    SearchRequest retrieveAttributes(Collection<String> attributesToRetrieve);

    SearchRequest cropAllRetrievedAttributes();

    SearchRequest cropAttributes(Collection<String> attributesToCrop);

    SearchRequest cropLength(int cropLength);

    SearchRequest markCropBoundariesWith(String cropMarker);

    SearchRequest highlightAllRetrievedAttributes();

    SearchRequest highlightAttributes(Collection<String> attributesToHighlight);

    SearchRequest highlightTags(String preTag, String postTag);

    AroundPoint aroundPoint(double lat, double lon);

    interface AroundPoint {
        SearchRequest withinDistanceInMeters(int distanceInMeters);
    }
}
