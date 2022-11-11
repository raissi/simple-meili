package org.raissi.meilisearch.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@Builder
public class MatchPosition {
    int start;
    int length;
}
