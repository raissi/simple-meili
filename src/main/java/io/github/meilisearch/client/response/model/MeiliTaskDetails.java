package io.github.meilisearch.client.response.model;

import lombok.Data;

@Data
public class MeiliTaskDetails {

    private Integer providedIds;
    private Integer deletedDocuments;
    private String originalFilter;

    //For index tasks
    private Integer receivedDocuments;
    private Integer indexedDocuments;

}
