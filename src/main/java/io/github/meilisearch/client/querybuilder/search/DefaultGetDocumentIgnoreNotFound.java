package io.github.meilisearch.client.querybuilder.search;

import java.util.List;
import java.util.Objects;

public class DefaultGetDocumentIgnoreNotFound extends BaseGet implements GetDocumentIgnoreNotFound{

    public DefaultGetDocumentIgnoreNotFound(String index, String documentId) {
        super("/indexes/"+index+"/"+"documents/"+documentId);
        Objects.requireNonNull(documentId, "When getting by document id, must give a value");
    }

    @Override
    public GetDocumentIgnoreNotFound fetchOnly(List<String> fields) {
        this.fields = String.join(",", fields);
        return this;
    }
}
