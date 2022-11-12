package org.raissi.meilisearch.client.querybuilder;

import org.raissi.meilisearch.client.querybuilder.delete.*;
import org.raissi.meilisearch.client.querybuilder.insert.DefaultOverrideDocuments;
import org.raissi.meilisearch.client.querybuilder.insert.DefaultUpsertDocuments;
import org.raissi.meilisearch.client.querybuilder.insert.OverrideDocuments;
import org.raissi.meilisearch.client.querybuilder.insert.UpsertDocuments;
import org.raissi.meilisearch.client.querybuilder.search.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultQueryBuilder implements FromIndex, IntoIndex {

    public static final String SEARCH_PHRASE_DELIM = "\"";
    private final String index;

    public DefaultQueryBuilder(String index) {
        Objects.requireNonNull(index, "index can not be null");
        this.index = index;
    }


    @Override
    public GetDocuments get() {
        return new DefaultGetDocuments(index);
    }

    @Override
    public GetDocument get(String documentId) {
        return new DefaultGetDocument(index, documentId);
    }

    @Override
    public SearchRequest q(String q) {
        return new DefaultSearchRequest(index).q(q);
    }

    @Override
    public SearchRequest phrase(String phrase) {
        String surroundedPhrase;
        if (!phrase.startsWith(SEARCH_PHRASE_DELIM)) {
            surroundedPhrase = SEARCH_PHRASE_DELIM +phrase+ SEARCH_PHRASE_DELIM;
        } else {
            surroundedPhrase = phrase;
        }
        return new DefaultSearchRequest(index).q(surroundedPhrase);
    }

    @Override
    public SearchRequest filter(String filter) {
        return new DefaultSearchRequest(index).filter(filter);
    }

    @Override
    public SearchRequest appendFilters(Collection<String> filters) {
        return new DefaultSearchRequest(index).appendFilters(filters);
    }

    @Override
    public SearchRequest facet(String facet) {
        return new DefaultSearchRequest(index).facet(facet);
    }

    @Override
    public SearchRequest facets(Collection<String> facets) {
        return new DefaultSearchRequest(index).facets(facets);
    }

    @Override
    public SearchRequest retrieveAttributes(Collection<String> attributesToRetrieve) {
        return new DefaultSearchRequest(index).retrieveAttributes(attributesToRetrieve);
    }

    @Override
    public SearchRequest cropAttributes(Collection<String> attributesToCrop) {
        return new DefaultSearchRequest(index).cropAttributes(attributesToCrop);
    }

    @Override
    public SearchRequest cropLength(int cropLength) {
        return new DefaultSearchRequest(index).cropLength(cropLength);
    }

    @Override
    public SearchRequest markCropBoundariesWith(String cropMarker) {
        return new DefaultSearchRequest(index).markCropBoundariesWith(cropMarker);
    }

    @Override
    public SearchRequest sortBy(String field, SortOrder sortOrder) {
        return new DefaultSearchRequest(index).sortBy(field, sortOrder);
    }

    @Override
    public SearchRequest sortAscBy(String field) {
        return new DefaultSearchRequest(index).sortAscBy(field);
    }

    @Override
    public SearchRequest sortDescBy(String field) {
        return new DefaultSearchRequest(index).sortDescBy(field);
    }

    @Override
    public SearchRequest sortByDistanceFromPoint(double lat, double lon, SortOrder sortOrder) {
        return new DefaultSearchRequest(index).sortByDistanceFromPoint(lat, lon, sortOrder);
    }

    @Override
    public SearchRequest.AroundPoint aroundPoint(double lat, double lon) {
        return new DefaultSearchRequest(index).aroundPoint(lat, lon);
    }

    @Override
    public <T> OverrideDocuments<T> overrideDocuments(List<T> documents) {
        return new DefaultOverrideDocuments<>(this.index, documents);
    }

    @Override
    public <T> UpsertDocuments upsertDocuments(List<T> documents) {
        return new DefaultUpsertDocuments<>(this.index, documents);
    }

    @Override
    public DeleteAllDocuments deleteAll() {
        return new DefaultDeleteAllDocuments(this.index);
    }

    @Override
    public <T> DeleteOneDocument delete(T uid) {
        return new DefaultDeleteOneDocument<>(this.index, uid);
    }

    @Override
    public <T> DeleteDocumentsByIds delete(Collection<T> ids) {
        List<String> documentsIds = ids.stream().map(String::valueOf).collect(Collectors.toList());
        return new DefaultDeleteDocumentsByIds(this.index, documentsIds);
    }
}
