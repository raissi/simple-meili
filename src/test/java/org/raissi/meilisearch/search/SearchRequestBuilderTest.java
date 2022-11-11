package org.raissi.meilisearch.search;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.raissi.meilisearch.client.querybuilder.MeiliQueryBuilder;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Arrays;

public class SearchRequestBuilderTest {

    @Test
    void shouldGenerateGeoRadius() {
        String queryJson = MeiliQueryBuilder.fromIndex("index")
                .aroundPoint(45.472735, 9.184019)
                .withinDistanceInMeters(2000)
                .json();
        Assertions.assertTrue(queryJson.contains("_geoRadius(45.472735, 9.184019, 2000)"));
    }

    @Test
    void shouldAddFilters() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("test")
                .filter("field = value")
                .json();

        JSONAssert.assertEquals("{\"q\": \"test\", \"filter\": [\"field = value\"]}", json, false);
    }
    @Test
    void shouldClearFilters() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("test")
                .filter("field = value")
                .clearFilters()
                .json();

        JSONAssert.assertEquals("{\"q\": \"test\"}", json, false);
    }

    @Test
    void shouldDefineRetrievedAttributes() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("")
                .filter("")
                .retrieveAttributes(Arrays.asList("attr1", "attr2"))
                .json();

        JSONAssert.assertEquals("{\"attributesToRetrieve\": [\"attr1\", \"attr2\"]}", json, false);
    }

    @Test
    void shouldUseDefaultCropParams() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("")
                .filter("")
                .json();

        Assertions.assertFalse(json.contains("attributesToCrop"));
        Assertions.assertFalse(json.contains("cropLength"));
        JSONAssert.assertNotEquals("{\"cropMarker\": \"…\"}", json, false);
    }

    @Test
    void shouldDefineCropParams() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("")
                .filter("")
                .cropAttributes(Arrays.asList("attr1", "attr2"))
                .cropLength(3)
                .markCropBoundariesWith("#")
                .json();

        JSONAssert.assertEquals("{\"attributesToCrop\": [\"attr1\", \"attr2\"]}", json, false);
        JSONAssert.assertEquals("{\"cropLength\": 3}", json, false);
        JSONAssert.assertEquals("{\"cropMarker\": \"#\"}", json, false);
    }

    @Test
    void shouldSetMatchesPosition() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("")
                .showMatchesPosition(true)
                .json();

        JSONAssert.assertEquals("{\"showMatchesPosition\":true}", json, false);
    }
}
