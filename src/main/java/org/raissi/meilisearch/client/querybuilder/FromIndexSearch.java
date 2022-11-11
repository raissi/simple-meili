package org.raissi.meilisearch.client.querybuilder;

import org.raissi.meilisearch.client.querybuilder.search.SearchRequest;

import java.util.Collection;

public interface FromIndexSearch<SRequest extends FromIndexSearch<SRequest>> {

    SearchRequest q(String q);

    /**
     * Automatically surround the given phrase with {@code \" \"}.
     * If the given phrase does already start with a \" it will be passed as is
     * @param phrase the search phrase
     * @return an instance of {@link SearchRequest}
     */
    SRequest phrase(String phrase);
    SRequest filter(String filter);
    SRequest filters(Collection<String> filters);

    SRequest retrieveAttributes(Collection<String> attributesToRetrieve);
    SRequest cropAttributes(Collection<String> attributesToCrop);
    SRequest cropLength(int cropLength);
    SRequest markCropBoundariesWith(String cropMarker);

    SRequest sortBy(String field, SortOrder sortOrder);
    SRequest sortAscBy(String field);
    SRequest sortDescBy(String field);

    SRequest sortByDistanceFromPoint(double lat, double lon, SortOrder sortOrder);

    SearchRequest.AroundPoint aroundPoint(double lat, double lon);
}
