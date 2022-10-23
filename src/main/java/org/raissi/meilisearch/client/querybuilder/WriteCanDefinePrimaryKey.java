package org.raissi.meilisearch.client.querybuilder;

import org.raissi.meilisearch.client.querybuilder.insert.WriteRequestWithBody;

import java.util.Optional;

public interface WriteCanDefinePrimaryKey<T, SelfT extends WriteCanDefinePrimaryKey<T, SelfT>> extends WriteRequestWithBody<T> {

    SelfT withPrimaryKey(String primaryKey);

    Optional<String> primaryKey();
}
