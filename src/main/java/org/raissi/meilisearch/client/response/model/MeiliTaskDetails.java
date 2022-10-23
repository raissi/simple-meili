package org.raissi.meilisearch.client.response.model;

public class MeiliTaskDetails {
    private Integer receivedDocumentIds;
    private Integer deletedDocuments;


    public Integer getReceivedDocumentIds() {
        return receivedDocumentIds;
    }

    public void setReceivedDocumentIds(Integer receivedDocumentIds) {
        this.receivedDocumentIds = receivedDocumentIds;
    }

    public Integer getDeletedDocuments() {
        return deletedDocuments;
    }

    public void setDeletedDocuments(Integer deletedDocuments) {
        this.deletedDocuments = deletedDocuments;
    }
}
