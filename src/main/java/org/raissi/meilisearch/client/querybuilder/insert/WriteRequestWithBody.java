package org.raissi.meilisearch.client.querybuilder.insert;

import org.raissi.meilisearch.client.querybuilder.HasBody;

import java.util.Optional;

public interface WriteRequestWithBody<T> extends WriteRequest, HasBody {

}
