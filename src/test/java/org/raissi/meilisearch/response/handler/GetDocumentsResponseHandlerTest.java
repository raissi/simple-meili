package org.raissi.meilisearch.response.handler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.raissi.meilisearch.client.querybuilder.search.GetDocument;
import org.raissi.meilisearch.client.querybuilder.search.GetDocuments;
import org.raissi.meilisearch.client.response.exceptions.MeiliSearchException;
import org.raissi.meilisearch.client.response.exceptions.NotFoundException;
import org.raissi.meilisearch.client.response.handler.GetDocumentResponseHandler;
import org.raissi.meilisearch.client.response.handler.GetDocumentsResponseHandler;
import org.raissi.meilisearch.client.response.handler.ResponseHandler;

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
