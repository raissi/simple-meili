package io.github.meilisearch.client.response.model;

import lombok.Data;

@Data
public class MeiliTaskDetails {
    private Integer receivedDocumentIds;
    private Integer deletedDocuments;

    //For index tasks
    private Integer receivedDocuments;
    private Integer indexedDocuments;

}
