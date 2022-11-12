package org.raissi.meilisearch.integration;

import okhttp3.OkHttpClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.raissi.meilisearch.client.MeiliClient;
import org.raissi.meilisearch.client.MeiliClientOkHttp;
import org.raissi.meilisearch.client.querybuilder.MeiliQueryBuilder;
import org.raissi.meilisearch.client.querybuilder.insert.UpsertDocuments;
import org.raissi.meilisearch.client.querybuilder.search.SearchRequest;
import org.raissi.meilisearch.client.response.exceptions.NotFoundException;
import org.raissi.meilisearch.client.response.handler.CanBlockOnTask;
import org.raissi.meilisearch.client.response.model.MeiliTask;
import org.raissi.meilisearch.model.Author;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.*;
import static org.raissi.meilisearch.client.response.model.MeiliAsyncWriteResponse.TASK_SUCCEEDED;

@Testcontainers
public class EndToEndITest {

    @Container
    public static GenericContainer meili = new GenericContainer(DockerImageName.parse("getmeili/meilisearch:v0.29.1"))
            .withExposedPorts(7700)
            .withReuse(true)
            .withEnv("MEILI_MASTER_KEY", "masterKey")
            .withEnv("MEILI_ENV", "development");

    public static final String AUTHORS_END_TO_END = "authorsEndToEnd";

    static MeiliClient client;
    static String hostUrl;

    @BeforeAll
    public static void setUp() throws Exception {
        String host = meili.getHost();
        Integer port = meili.getFirstMappedPort();
        hostUrl = "http://" + host + ":" + port;
        OkHttpClient okHttpClient = new OkHttpClient();

        client = MeiliClientOkHttp.usingOkHttp(okHttpClient)
                //.withJsonWriter(jsonWriter).andJsonReader(jsonReader)
                .forHost(hostUrl)
                .withSearchKey("masterKey");

        MeiliClientOkHttp ssss = MeiliClientOkHttp.usingOkHttp(okHttpClient)
                .forHost(hostUrl)
                .withJsonWriter(null).andJsonReader(null)
                .withSearchKey("masterKey")
                ;


    }

    @Test
    void doEndToEnd() {
        //1. Index the data
        AtomicBoolean upsertSuccess = new AtomicBoolean(false);
        UpsertDocuments<Author> upsert = MeiliQueryBuilder.intoIndex(AUTHORS_END_TO_END).upsertDocuments(authors()).withPrimaryKey("uid");
        Optional<MeiliTask> insertResult = client.upsert(upsert)
                .andThen(CanBlockOnTask::waitForCompletion)
                .ifSuccess(s -> upsertSuccess.set(true))
                .ifFailure(s -> upsertSuccess.set(false))
                .ignoreErrors();

        assertThat(upsertSuccess).isTrue();
        assertThat(insertResult).hasValueSatisfying(res -> {
            assertThat(res.getStatus()).isEqualTo(TASK_SUCCEEDED);
        });

        //2. search the data:
        SearchRequest uid4Request = MeiliQueryBuilder.fromIndex(AUTHORS_END_TO_END)
                .filter("uid = 4")
                ;
    }

    public static List<Author> authors() {
        Author austen = new Author();
        austen.setUid("1");
        austen.setName("Jane Austen");
        austen.setCountry("England");
        austen.setBio("Jane Austen was an English novelist known primarily " +
                "for her six major novels, which interpret, critique, " +
                "and comment upon the British landed gentry at the end " +
                "of the 18th century. Austen's plots often explore the dependence " +
                "of women on marriage in the pursuit of favourable social standing and economic security.");

        Author dickens = new Author();
        dickens.setUid("2");
        dickens.setName("Charles Dickens");
        dickens.setCountry("England");
        dickens.setBio("Charles John Huffam Dickens was an English writer and social critic. " +
                "He created some of the world's best-known fictional characters and is " +
                "regarded by many as the greatest novelist of the Victorian era.");

        Author flaubert = new Author();
        flaubert.setUid("3");
        flaubert.setName("Gustave Flaubert");
        flaubert.setCountry("France");
        flaubert.setBio("Gustave Flaubert was a French novelist. Highly influential, " +
                "he has been considered the leading exponent of literary realism in his country.");

        Author darwin = new Author();
        darwin.setUid("4");
        darwin.setName("Charles Darwin");
        darwin.setCountry("England");
        darwin.setBio("Charles Robert Darwin FRS FRGS FLS FZS JP was an English naturalist, " +
                "geologist, and biologist, widely known for contributing to the understanding of evolutionary biology.");

        Author baudelaire = new Author();
        baudelaire.setUid("5");
        baudelaire.setName("Charles Baudelaire");
        baudelaire.setCountry("France");
        baudelaire.setBio("Charles Pierre Baudelaire was a French poet who also produced notable work as an essayist " +
                "and art critic. His poems exhibit mastery in the handling of rhyme and rhythm, contain an exoticism " +
                "inherited from Romantics, but are based on observations of real life.");

        return List.of(austen, dickens, flaubert, darwin, baudelaire);
    }
}
