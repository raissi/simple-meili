package org.raissi.meilisearch.client.querybuilder.search;

import org.raissi.meilisearch.client.querybuilder.FromIndexSearch;
import org.raissi.meilisearch.client.querybuilder.HasBody;
import org.raissi.meilisearch.client.querybuilder.MatchingStrategy;

import java.util.Collection;

public interface SearchRequest extends FromIndexSearch<SearchRequest>, PagingRequest<SearchRequest>, HasBody {
    //TODO add method to append query terms e.g: \"african american\" horror

    SearchRequest appendToQuery(String q);

    SearchRequest appendPhraseToQuery(String q);

    SearchRequest clearFilters();

    SearchRequest facet(String facet);
    SearchRequest facets(Collection<String> facets);

    SearchRequest cropAllRetrievedAttributes();

    SearchRequest highlightAllRetrievedAttributes();

    SearchRequest highlightAttributes(Collection<String> attributesToHighlight);

    SearchRequest highlightTags(String preTag, String postTag);

    SearchRequest showMatchesPosition(boolean showMatchesPosition);

    SearchRequest matchDocumentsContainingAllQueryTerms();

    SearchRequest matchingStrategy(MatchingStrategy matchingStrategy);

    interface AroundPoint {
        SearchRequest withinDistanceInMeters(int distanceInMeters);
    }
}
