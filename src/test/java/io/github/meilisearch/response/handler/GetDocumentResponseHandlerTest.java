package io.github.meilisearch.response.handler;

import io.github.meilisearch.client.querybuilder.search.GetDocument;
import io.github.meilisearch.client.response.exceptions.MeiliSearchException;
import io.github.meilisearch.client.response.exceptions.NotFoundException;
import io.github.meilisearch.client.response.handler.GetDocumentResponseHandler;
import io.github.meilisearch.client.response.handler.ResponseHandler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Optional;

public class GetDocumentResponseHandlerTest {

    @Test
    void shouldReturnNotFoundExceptionOn404() {
        GetDocument request = Mockito.mock(GetDocument.class);
        ResponseHandler handler = new GetDocumentResponseHandler(request);
        Optional<MeiliSearchException> exception = handler.buildException(404, "indexes", Map.of(), "");

        Assertions.assertThat(exception).hasValueSatisfying(e -> Assertions.assertThat(e).isInstanceOf(NotFoundException.class));
    }

    @Test
    void shouldReturnMeiliSearchExceptionOnOtherThan404() {
        GetDocument request = Mockito.mock(GetDocument.class);
        ResponseHandler handler = new GetDocumentResponseHandler(request);
        Optional<MeiliSearchException> exception = handler.buildException(400, "indexes", Map.of(), "");

        Assertions.assertThat(exception).hasValueSatisfying(e -> Assertions.assertThat(e).isInstanceOf(MeiliSearchException.class));
    }
}
