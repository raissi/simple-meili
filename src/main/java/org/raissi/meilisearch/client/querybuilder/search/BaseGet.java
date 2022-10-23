package org.raissi.meilisearch.client.querybuilder.search;

public abstract class BaseGet {

    protected final String completePath;
    protected String fields = "*";

    protected BaseGet(String path) {
        this.completePath = path;
    }

    public String path() {
        return this.completePath;
    }

    public String fields() {
        return fields;
    }
}
