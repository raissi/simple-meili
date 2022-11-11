package org.raissi.meilisearch.integration;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeAll;
import org.raissi.meilisearch.client.MeiliClient;
import org.raissi.meilisearch.client.MeiliClientOkHttp;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.raissi.meilisearch.client.MeiliClientOkHttp.JSON;

@Testcontainers
public abstract class BaseIntTest {

    @Container
    public static GenericContainer meili = new GenericContainer(DockerImageName.parse("getmeili/meilisearch:v0.29.1"))
            .withExposedPorts(7700)
            .withReuse(true)
            .withEnv("MEILI_MASTER_KEY", "masterKey")
            .withEnv("MEILI_ENV", "development");


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
                .withSearchKey("masterKey");

        URL url = Thread.currentThread().getContextClassLoader().getResource("movies.json");
        String movies = Files.readString(Paths.get(url.getFile()));

        Request request = new Request.Builder()
                .url(hostUrl+"/indexes/movies/documents")
                .addHeader("Authorization", "Bearer masterKey")
                .post(RequestBody.create(movies, JSON))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException();
            }
            Thread.sleep(1000);
        }
    }

}
