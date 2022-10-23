package org.raissi.meilisearch.client.querybuilder.insert;

import org.raissi.meilisearch.client.querybuilder.HasBody;
import org.raissi.meilisearch.client.querybuilder.MeiliRequest;

import java.util.Optional;

public interface WriteRequest<T, SelfT extends WriteRequest<T, SelfT>> extends MeiliRequest, HasBody {

    SelfT withPrimaryKey(String primaryKey);

    Optional<String> primaryKey();
}
