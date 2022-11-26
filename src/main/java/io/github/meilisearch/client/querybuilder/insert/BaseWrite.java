package io.github.meilisearch.client.querybuilder.insert;

public abstract class BaseWrite {

    protected final String completePath;

    public BaseWrite(String index) {
        this(index, false);
    }

    public BaseWrite(String indexOrPath, boolean completePathComputed) {
        if(!completePathComputed) {
            this.completePath = "/indexes/"+indexOrPath+"/"+"documents";
        } else {
            this.completePath = indexOrPath;
        }
    }

    public BaseWrite(String index, String id) {
        this.completePath = "/indexes/"+index+"/"+"documents/"+id;
    }

    public String path() {
        return this.completePath;
    }


}
