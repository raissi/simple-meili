package org.raissi.meilisearch.client.response.model;

import lombok.Data;

import java.util.List;

@Data
public class GetResults<T> {

    private List<T> results;
    private int offset;
    private int limit;
    private long total;

}
