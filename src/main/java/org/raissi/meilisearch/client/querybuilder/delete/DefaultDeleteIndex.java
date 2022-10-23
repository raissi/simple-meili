package org.raissi.meilisearch.client.querybuilder.delete;

public class DefaultDeleteIndex implements DeleteIndex {

    private final String completePath;
    public DefaultDeleteIndex(String index) {
        this.completePath = "/indexes/"+index;
    }

    @Override
    public String path() {
        return this.completePath;
    }
}
