package org.raissi.meilisearch.client.querybuilder.insert;

public abstract class BaseWrite {

    protected final String completePath;

    public BaseWrite(String index) {
        this.completePath = "/indexes/"+index+"/"+"documents";
    }

    public BaseWrite(String index, String id) {
        this.completePath = "/indexes/"+index+"/"+"documents/"+id;
    }

    public String path() {
        return this.completePath;
    }


}
