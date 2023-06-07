package io.github.meilisearch.integration;

import io.github.meilisearch.client.MeiliClientOkHttp;
import io.github.meilisearch.client.querybuilder.MeiliQueryBuilder;
import io.github.meilisearch.client.querybuilder.delete.DeleteDocumentsByFilter;
import io.github.meilisearch.client.querybuilder.search.GetDocuments;
import io.github.meilisearch.client.querybuilder.search.SearchRequest;
import io.github.meilisearch.client.response.handler.CanBlockOnTask;
import io.github.meilisearch.client.response.model.MeiliTask;
import io.github.meilisearch.client.response.model.SearchResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import io.github.meilisearch.client.MeiliClient;
import io.github.meilisearch.client.querybuilder.insert.UpsertDocuments;
import io.github.meilisearch.client.querybuilder.search.JacksonJsonReaderWriter;
import io.github.meilisearch.client.response.model.GetResults;
import io.github.meilisearch.model.Author;
import io.github.meilisearch.model.Author.AuthorFormatted;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.*;
import static io.github.meilisearch.client.response.model.MeiliAsyncWriteResponse.TASK_SUCCEEDED;

@Testcontainers
public class EndToEndITest {

    static JacksonJsonReaderWriter jsonReaderWriter = new JacksonJsonReaderWriter();
    @Container
    public static GenericContainer meili = new GenericContainer(DockerImageName.parse("getmeili/meilisearch:v1.2.0"))
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
                .forHost(hostUrl)
                .withJsonWriter(jsonReaderWriter).andJsonReader(jsonReaderWriter)//This is default, so may be omitted if using Jackson
                .withSearchKey("masterKey");
    }

    @Test
    void doEndToEnd() throws Exception {
        //1. Index the data
        AtomicBoolean upsertSuccess = new AtomicBoolean(false);
        UpsertDocuments upsert = MeiliQueryBuilder.intoIndex(AUTHORS_END_TO_END).upsertDocuments(authors()).withPrimaryKey("uid");
        Optional<MeiliTask> insertResult = client.upsert(upsert)
                .andThen(CanBlockOnTask::waitForCompletion)
                .ifSuccess(s -> upsertSuccess.set(true))
                .ifFailure(s -> upsertSuccess.set(false))
                .ignoreErrors();

        assertThat(upsertSuccess).isTrue();
        assertThat(insertResult).hasValueSatisfying(res -> {
            assertThat(res.getStatus()).isEqualTo(TASK_SUCCEEDED);
        });

        // Define filterable attributes: for now simple-meili does not support admin operations
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(hostUrl+"/indexes/"+AUTHORS_END_TO_END+"/settings/filterable-attributes")
                .addHeader("Authorization", "Bearer masterKey")
                .put(RequestBody.create("[\"country\", \"uid\"]", MeiliClientOkHttp.JSON))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException();
            }
            Thread.sleep(300);
        }

        //2. search the data:
        SearchRequest uid3Request = MeiliQueryBuilder.fromIndex(AUTHORS_END_TO_END)
                .filter("uid = 3");

        List<Author> authorsFromSearchRes = client.search(uid3Request, Author.class)
                .andThenTry(SearchResponse::getHits)
                .orElse(Collections::emptyList);
        assertThat(authorsFromSearchRes).hasSize(1);
        assertThat(authorsFromSearchRes).allMatch(author -> author.getUid().equals("3"));

        //3. Delete a doc
        AtomicBoolean deleteSuccess = new AtomicBoolean(false);
        Optional<MeiliTask> deleteResult = client.deleteByIds(AUTHORS_END_TO_END, Collections.singleton("3"))
                .andThen(CanBlockOnTask::waitForCompletion)
                .ifSuccess(s -> deleteSuccess.set(true))
                .ignoreErrors();//Do not ignore errors :-P
        assertThat(deleteSuccess).isTrue();
        assertThat(deleteResult).hasValueSatisfying(res -> {
            assertThat(res.getStatus()).isEqualTo(TASK_SUCCEEDED);
            assertThat(res.getOperationType()).isEqualTo("documentDeletion");
        });

        //4. Get all documents
        GetDocuments getDocuments = MeiliQueryBuilder.fromIndex(AUTHORS_END_TO_END).get().fetch(5).startingAt(0);
        List<Author> authorsFromGetDocuments = client.get(getDocuments, Author.class)
                .andThenTry(GetResults::getResults)
                .orElse(Collections::emptyList);
        assertThat(authorsFromGetDocuments).hasSize(4);

        //5. Search docs and get highlights
        SearchRequest searchGeology = MeiliQueryBuilder.fromIndex(AUTHORS_END_TO_END).q("geology").highlightAllRetrievedAttributes();
        List<AuthorFormatted> authorsFromSearchGeology = client.search(searchGeology, AuthorFormatted.class)
                .andThenTry(SearchResponse::getHits)
                .ifFailure(Throwable::printStackTrace)
                .orElse(Collections::emptyList);
        assertThat(authorsFromSearchGeology).hasSize(1);
        assertThat(authorsFromSearchGeology).allMatch(author -> author.getUid().equals("4"));

        //6. Delete authors from England:
        AtomicBoolean filterDeletion = new AtomicBoolean(false);
        DeleteDocumentsByFilter deleteDocumentsByFilter = MeiliQueryBuilder.fromIndex(AUTHORS_END_TO_END).deleteByFilter("country = England");
        Optional<MeiliTask> deleteByFilterResult = client.deleteByFilter(deleteDocumentsByFilter)
                .andThen(CanBlockOnTask::waitForCompletion)
                .ifSuccess(s -> filterDeletion.set(true))
                .ignoreErrors();//Do not ignore errors :-P
        assertThat(filterDeletion).isTrue();
        assertThat(deleteByFilterResult).hasValueSatisfying(res -> {
            assertThat(res.getStatus()).isEqualTo(TASK_SUCCEEDED);
            assertThat(res.getOperationType()).isEqualTo("documentDeletion");
        });

        //Last, we delete the index:
        Optional<MeiliTask> deleteIndexRes = client.deleteIndex(AUTHORS_END_TO_END)
                .andThen(CanBlockOnTask::waitForCompletion)
                .ignoreErrors();
        assertThat(deleteIndexRes).hasValueSatisfying(res -> {
            assertThat(res.getStatus()).isEqualTo(TASK_SUCCEEDED);
            assertThat(res.getOperationType()).isEqualTo("indexDeletion");
        });
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
