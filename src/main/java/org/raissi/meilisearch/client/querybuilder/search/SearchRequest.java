package org.raissi.meilisearch.client.querybuilder.search;

import org.raissi.meilisearch.client.querybuilder.HasBody;

import java.util.Collection;
import java.util.List;

public interface SearchRequest extends PagingRequest<SearchRequest>, HasBody {
    //TODO add method to append query terms e.g: \"african american\" horror

    SearchRequest q(String q);

    SearchRequest filter(String filter);
    SearchRequest filters(Collection<String> filters);
    SearchRequest facet(String facet);
    SearchRequest facets(Collection<String> facets);

    String query();
    List<String> filters();
}
