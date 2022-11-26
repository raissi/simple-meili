package io.github.meilisearch.client.querybuilder.search;

import java.util.List;
import java.util.Objects;

public class DefaultGetDocument extends BaseGet implements GetDocument {

    private final String documentId;
    protected final String index;

    public DefaultGetDocument(String index, String documentId) {
        super("/indexes/"+index+"/"+"documents/"+documentId);
        this.index = index;
        this.documentId = Objects.requireNonNull(documentId, "When getting by document id, must give a value");
    }

    @Override
    public GetDocumentIgnoreNotFound notFoundAsEmpty() {
        return new DefaultGetDocumentIgnoreNotFound(this.index, this.documentId);
    }

    @Override
    public GetDocument fetchOnly(List<String> fields) {
        this.fields = String.join(",", fields);
        return this;
    }
}
