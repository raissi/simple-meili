package org.raissi.meilisearch.client.querybuilder;

import org.raissi.meilisearch.client.querybuilder.insert.WriteRequestWithBody;

import java.util.Optional;

public interface WriteCanDefinePrimaryKey<SelfT extends WriteCanDefinePrimaryKey<SelfT>> extends WriteRequestWithBody {

    SelfT withPrimaryKey(String primaryKey);

    Optional<String> primaryKey();
}
