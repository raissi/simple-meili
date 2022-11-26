package io.github.meilisearch.response.handler;

import io.github.meilisearch.client.querybuilder.search.GetDocuments;
import io.github.meilisearch.client.response.exceptions.MeiliSearchException;
import io.github.meilisearch.client.response.exceptions.NotFoundException;
import io.github.meilisearch.client.response.handler.GetDocumentsResponseHandler;
import io.github.meilisearch.client.response.handler.ResponseHandler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Optional;

public class GetDocumentsResponseHandlerTest {

    @Test
    void shouldReturnNotFoundExceptionOn404() {
        GetDocuments request = Mockito.mock(GetDocuments.class);
        ResponseHandler handler = new GetDocumentsResponseHandler(request);
        Optional<MeiliSearchException> exception = handler.buildException(404, "indexes", Map.of(), "");

        Assertions.assertThat(exception).hasValueSatisfying(e -> Assertions.assertThat(e).isInstanceOf(NotFoundException.class));
    }

    @Test
    void shouldReturnMeiliSearchExceptionOnOtherThan404() {
        GetDocuments request = Mockito.mock(GetDocuments.class);
        ResponseHandler handler = new GetDocumentsResponseHandler(request);
        Optional<MeiliSearchException> exception = handler.buildException(400, "indexes", Map.of(), "");

        Assertions.assertThat(exception).hasValueSatisfying(e -> Assertions.assertThat(e).isInstanceOf(MeiliSearchException.class));
    }
}
