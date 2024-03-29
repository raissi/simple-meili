package io.github.meilisearch.search;

import io.github.meilisearch.client.querybuilder.MatchingStrategy;
import io.github.meilisearch.client.querybuilder.SortOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.meilisearch.client.querybuilder.MeiliQueryBuilder;
import io.github.meilisearch.client.querybuilder.search.JacksonJsonReaderWriter;
import io.github.meilisearch.client.querybuilder.search.JsonWriter;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Arrays;
import java.util.Collections;

public class SearchRequestBuilderTest {

    JsonWriter jsonWriter = new JacksonJsonReaderWriter();
    @Test
    void shouldGenerateGeoRadius() {
        String queryJson = MeiliQueryBuilder.fromIndex("index")
                .aroundPoint(45.472735, 9.184019)
                .withinDistanceInMeters(2000)
                .json(jsonWriter);
        Assertions.assertTrue(queryJson.contains("_geoRadius(45.472735, 9.184019, 2000)"));
    }

    @Test
    void shouldAddPhrase() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .phrase("some query")
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"q\": \"\\\"some query\\\"\"}", json, false);
    }
    @Test
    void shouldAddPhrase_InsideRequest() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("")
                .phrase("some query")
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"q\": \"\\\"some query\\\"\"}", json, false);
    }

    @Test
    void shouldAppendToQuery() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("hello")
                .appendToQuery("world")
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"q\": \"hello world\"}", json, false);
    }

    @Test
    void shouldAppendPhraseToQuery() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("hello")
                .appendPhraseToQuery("you there")
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"q\": \"hello \\\"you there\\\"\"}", json, false);
    }

    @Test
    void shouldAddFilters() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("test")
                .filter("field = value")
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"q\": \"test\", \"filter\": [\"field = value\"]}", json, false);
    }
    @Test
    void shouldAddFilters_collection() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("test")
                .appendFilters(Arrays.asList("field = value", "field2 = value2"))
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"q\": \"test\", \"filter\": [\"field = value\", \"field2 = value2\"]}", json, false);
    }

    @Test
    void shouldDefineFacets() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .facets(Arrays.asList("field", "field2"))
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"facets\": [\"field\", \"field2\"]}", json, false);
    }

    @Test
    void shouldDefineFacets_insideRequest() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("test")
                .facet("field")
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"q\": \"test\", \"facets\": [\"field\"]}", json, false);
    }
    @Test
    void shouldClearFilters() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("test")
                .filter("field = value")
                .clearFilters()
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"q\": \"test\"}", json, false);
    }

    @Test
    void shouldDefineRetrievedAttributes() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("")
                .filter("")
                .retrieveAttributes(Arrays.asList("attr1", "attr2"))
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"attributesToRetrieve\": [\"attr1\", \"attr2\"]}", json, false);
    }

    @Test
    void shouldNotDefineRetrievedAttributesWhenEmptyCollection() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("")
                .retrieveAttributes(Collections.emptyList())
                .json(jsonWriter);

        JSONAssert.assertEquals("{}", json, false);
    }

    @Test
    void shouldDefineRetrievedAttributes_UsingFetchOnly() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("")
                .filter("")
                .fetchOnly(Arrays.asList("attr1", "attr2"))
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"attributesToRetrieve\": [\"attr1\", \"attr2\"]}", json, false);
    }

    @Test
    void shouldDefinePaging() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("")
                .startingAt(3)
                .fetch(25)
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"offset\":3,\"limit\":25}", json, false);
    }

    @Test
    void shouldNotUseDefaultCropParams() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("")
                .filter("")
                .json(jsonWriter);

        Assertions.assertFalse(json.contains("attributesToCrop"));
        Assertions.assertFalse(json.contains("cropLength"));
        JSONAssert.assertNotEquals("{\"cropMarker\": \"…\"}", json, false);
    }

    @Test
    void shouldCropAllParams() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("")
                .cropAllRetrievedAttributes()
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"cropMarker\":\"…\",\"attributesToCrop\":[\"*\"]}", json, false);
    }

    @Test
    void shouldDefineCropParams() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("")
                .filter("")
                .cropAttributes(Arrays.asList("attr1", "attr2"))
                .cropLength(3)
                .markCropBoundariesWith("#")
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"attributesToCrop\": [\"attr1\", \"attr2\"]}", json, false);
        JSONAssert.assertEquals("{\"cropLength\": 3}", json, false);
        JSONAssert.assertEquals("{\"cropMarker\": \"#\"}", json, false);
    }

    @Test
    public void shouldHighlightAllAttributes() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("")
                .highlightAllRetrievedAttributes()
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"attributesToHighlight\": [\"*\"]}", json, false);
        JSONAssert.assertEquals("{\"highlightPreTag\": \"<em>\"}", json, false);
        JSONAssert.assertEquals("{\"highlightPostTag\": \"</em>\"}", json, false);
    }

    @Test
    void shouldSetMatchesPosition() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("search")
                .showMatchesPosition(true)
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"showMatchesPosition\":true}", json, false);
    }

    @Test
    void shouldAddSort() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .sortAscBy("uid")
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"sort\":[\"uid:asc\"]}", json, false);
    }

    @Test
    void shouldAddSort_Desc() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .sortDescBy("uid")
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"sort\":[\"uid:desc\"]}", json, false);
    }

    @Test
    void shouldAddSort_InsideRequest() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("search")
                .sortAscBy("uid")
                .sortDescBy("name")
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"sort\":[\"uid:asc\", \"name:desc\"]}", json, false);
    }

    @Test
    void shouldAddSort_WithGeoPoint_InsideRequest() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("search")
                .sortAscBy("uid")
                .sortByDistanceFromPoint(48.8561446, 2.2978204, SortOrder.ASC)
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"sort\":[\"uid:asc\", \"_geoPoint(48.8561446, 2.2978204):asc\"]}", json, false);
    }

    @Test
    public void shouldDefineMatchingStrategy() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("search")
                .matchingStrategy(MatchingStrategy.LAST)
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"matchingStrategy\": \"last\"}", json, false);
    }

    @Test
    public void shouldDefineMatchingStrategy_ALL() throws Exception {
        var json = MeiliQueryBuilder.fromIndex("index")
                .q("search")
                .matchDocumentsContainingAllQueryTerms()
                .json(jsonWriter);

        JSONAssert.assertEquals("{\"matchingStrategy\": \"all\"}", json, false);
    }
}
