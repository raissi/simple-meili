package org.raissi.meilisearch.client.querybuilder;

import org.raissi.meilisearch.client.querybuilder.insert.OverrideDocuments;

import java.util.List;

public interface IntoIndex {
    <T> OverrideDocuments<T> overrideDocuments(List<T> documents);
}
