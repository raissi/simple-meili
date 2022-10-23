package org.raissi.meilisearch.client.querybuilder;

import org.raissi.meilisearch.client.querybuilder.delete.DeleteIndex;

public interface Index {
    DeleteIndex delete();
}
