package org.raissi.meilisearch.client.querybuilder.insert;

import org.raissi.meilisearch.client.querybuilder.HasBody;
import org.raissi.meilisearch.client.querybuilder.MeiliRequest;

import java.util.Optional;

public interface OverrideDocuments<T> extends MeiliRequest, HasBody {

    OverrideDocuments<T> withPrimaryKey(String primaryKey);

    Optional<String> primaryKey();
}
