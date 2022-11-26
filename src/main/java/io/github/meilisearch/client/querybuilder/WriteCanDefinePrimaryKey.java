package io.github.meilisearch.client.querybuilder;

import io.github.meilisearch.client.querybuilder.insert.WriteRequestWithBody;

import java.util.Optional;

public interface WriteCanDefinePrimaryKey<SelfT extends WriteCanDefinePrimaryKey<SelfT>> extends WriteRequestWithBody {

    SelfT withPrimaryKey(String primaryKey);

    Optional<String> primaryKey();
}
