package org.raissi.meilisearch.client.querybuilder;

import org.raissi.meilisearch.client.querybuilder.insert.DefaultOverrideDocuments;
import org.raissi.meilisearch.client.querybuilder.insert.DefaultUpsertDocuments;
import org.raissi.meilisearch.client.querybuilder.insert.OverrideDocuments;
import org.raissi.meilisearch.client.querybuilder.insert.UpsertDocuments;
import org.raissi.meilisearch.client.querybuilder.search.DefaultGetDocument;
import org.raissi.meilisearch.client.querybuilder.search.DefaultGetDocuments;
import org.raissi.meilisearch.client.querybuilder.search.GetDocument;
import org.raissi.meilisearch.client.querybuilder.search.GetDocuments;

import java.util.List;
import java.util.Objects;

public class DefaultQueryBuilder implements FromIndex, IntoIndex {

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
    public <T> OverrideDocuments<T> overrideDocuments(List<T> documents) {
        return new DefaultOverrideDocuments<>(this.index, documents);
    }

    @Override
    public <T> UpsertDocuments<T> upsertDocuments(List<T> documents) {
        return new DefaultUpsertDocuments<>(this.index, documents);
    }
}
