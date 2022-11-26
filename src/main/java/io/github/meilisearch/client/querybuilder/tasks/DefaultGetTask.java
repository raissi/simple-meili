package io.github.meilisearch.client.querybuilder.tasks;

import io.github.meilisearch.client.querybuilder.search.BaseGet;

import java.util.List;

public class DefaultGetTask extends BaseGet implements GetTask {

    public DefaultGetTask(String taskUid) {
        super("/tasks/"+taskUid+"/");
    }

    @Override
    public GetTask fetchOnly(List<String> fields) {
        this.fields = String.join(",", fields);
        return this;
    }

    @Override
    public String fields() {
        return "*";
    }
}
