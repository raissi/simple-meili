package org.raissi.meilisearch.client.querybuilder.search;

import java.util.List;

public class DefaultGetDocuments extends BaseGet implements GetDocuments {

    private int offset = 0;
    private int limit = 20;

    public DefaultGetDocuments(String index) {
        super("/indexes/" + index + "/" + "documents/");
    }

    @Override
    public GetDocuments startingAt(int offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public GetDocuments fetch(int limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public int limit() {
        return limit;
    }

    @Override
    public int offset() {
        return offset;
    }

    @Override
    public GetDocuments fetchOnly(List<String> fields) {
        this.fields = String.join(",", fields);
        return this;
    }
}
