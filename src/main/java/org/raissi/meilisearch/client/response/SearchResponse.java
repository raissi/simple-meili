package org.raissi.meilisearch.client.response;

import io.vavr.control.Either;
import org.raissi.meilisearch.client.response.model.SearchResults;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SearchResponse<T> {
//FIXME split into MonoResult, and multiple results
    private final Either<SearchResults<T>, Optional<T>> results; //FIXME create our Either

    public Optional<T> unique() {
        //If it was a list with a single element, don't throw ?
        return results.getOrElseThrow(results ->
                new IllegalArgumentException("You asked for a unique results but there was a list. " +
                        "Please check if your original query was a get by id"));
    }

    public SearchResults<T> searchResults() {
        return results.getLeft();//TODO or throw
    }

    public List<T> list() {
        return results.fold(SearchResults::getResults,
                    res -> res.stream().collect(Collectors.toList()));
    }
    public SearchResponse(SearchResults<T> results) {
        this.results = Either.left(results);
    }
    public SearchResponse(T result) {
        this.results = Either.right(Optional.ofNullable(result));
    }

    @Override
    public String toString() {
        return "SearchResponse{" +
                "results=" + results +
                '}';
    }
}
