package org.raissi.meilisearch.response.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.raissi.meilisearch.client.querybuilder.search.GetDocumentIgnoreNotFound;
import org.raissi.meilisearch.client.response.exceptions.MeiliSearchException;
import org.raissi.meilisearch.client.response.exceptions.NotFoundException;
import org.raissi.meilisearch.client.response.handler.GetDocumentIgnoreNotFoundResponseHandler;
import org.raissi.meilisearch.client.response.handler.ResponseHandler;
import org.raissi.meilisearch.client.response.model.MeiliError;

import java.util.Map;
import java.util.Optional;

public class GetDocumentIgnoreNotFoundResponseHandlerTest {

    @Test
    void shouldReturnEmptyOnDocumentNotFound() throws Exception {
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        GetDocumentIgnoreNotFound getDocument = Mockito.mock(GetDocumentIgnoreNotFound.class);

        MeiliError meiliError = new MeiliError();
        meiliError.setCode(MeiliError.DOCUMENT_NOT_FOUND_ERROR);
        Mockito.when(objectMapper.readValue("someResponse", MeiliError.class))
                .thenReturn(meiliError);

        ResponseHandler handler = new GetDocumentIgnoreNotFoundResponseHandler(getDocument, objectMapper);

        Optional<MeiliSearchException> response = handler.buildException(404, "/indexes/movies/123", Map.of(), "someResponse");
        Assertions.assertThat(response).isEmpty();
    }

    @Test
    void shouldReturnNotFoundExceptionOnOtherThanDocumentNotFound() throws Exception {
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        GetDocumentIgnoreNotFound getDocument = Mockito.mock(GetDocumentIgnoreNotFound.class);

        MeiliError meiliError = new MeiliError();
        meiliError.setCode("some error code");
        Mockito.when(objectMapper.readValue("someResponse", MeiliError.class))
                .thenReturn(meiliError);

        ResponseHandler handler = new GetDocumentIgnoreNotFoundResponseHandler(getDocument, objectMapper);

        Optional<MeiliSearchException> response = handler.buildException(404, "/indexes/movies/123", Map.of(), "someResponse");
        Assertions.assertThat(response).hasValueSatisfying(e -> Assertions.assertThat(e).isInstanceOf(NotFoundException.class));
    }

    @Test
    void shouldReturnMeiliSearchExceptionOnOtherThan404() {
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        GetDocumentIgnoreNotFound getDocument = Mockito.mock(GetDocumentIgnoreNotFound.class);

        ResponseHandler handler = new GetDocumentIgnoreNotFoundResponseHandler(getDocument, objectMapper);
        Optional<MeiliSearchException> exception = handler.buildException(400, "indexes", Map.of(), "");

        Assertions.assertThat(exception).hasValueSatisfying(e -> Assertions.assertThat(e).isInstanceOf(MeiliSearchException.class));
    }
}
