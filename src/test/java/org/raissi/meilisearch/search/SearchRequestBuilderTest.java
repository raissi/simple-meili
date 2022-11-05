package org.raissi.meilisearch.search;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.raissi.meilisearch.client.querybuilder.MeiliQueryBuilder;

public class SearchRequestBuilderTest {

    @Test
    void shouldGenerateGeoRadius() {
        String geoRadius = MeiliQueryBuilder.fromIndex("index")
                .aroundPoint(45.472735, 9.184019)
                .withinDistanceInMeters(2000)
                .filters()
                .get(0);
        Assertions.assertEquals("_geoRadius(45.472735, 9.184019, 2000)", geoRadius);
    }
}
