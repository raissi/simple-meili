package org.raissi.meilisearch.client.querybuilder;

import org.raissi.meilisearch.client.querybuilder.tasks.DefaultGetTask;
import org.raissi.meilisearch.client.querybuilder.tasks.GetTask;

public class MeiliQueryBuilder {

    public static Index index(String index) {
        return new DefaultIndex(index);
    }

    public static FromIndex fromIndex(String index) {
        return new DefaultQueryBuilder(index);
    }

    public static IntoIndex intoIndex(String index) {
        return new DefaultQueryBuilder(index);
    }

    public static GetTask forTask(String taskUid) {
        return new DefaultGetTask(taskUid);
    }
}
