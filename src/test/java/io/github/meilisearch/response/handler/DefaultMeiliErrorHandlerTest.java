package io.github.meilisearch.response.handler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import io.github.meilisearch.client.querybuilder.MeiliRequest;
import io.github.meilisearch.client.response.exceptions.MeiliSearchException;
import io.github.meilisearch.client.response.exceptions.NotFoundException;
import io.github.meilisearch.client.response.handler.DefaultMeiliErrorHandler;
import io.github.meilisearch.client.response.handler.ResponseHandler;

import java.util.Map;
import java.util.Optional;

public class DefaultMeiliErrorHandlerTest {

    @Test
    void shouldReturnNotFoundExceptionOn404() {
        MeiliRequest request = Mockito.mock(MeiliRequest.class);
        ResponseHandler handler = new DefaultMeiliErrorHandler(request);
        Optional<MeiliSearchException> exception = handler.buildException(404, "indexes", Map.of(), "");

        Assertions.assertThat(exception).hasValueSatisfying(e -> Assertions.assertThat(e).isInstanceOf(NotFoundException.class));
    }

    @Test
    void shouldReturnMeiliSearchExceptionOnOtherThan404() {
        MeiliRequest request = Mockito.mock(MeiliRequest.class);
        ResponseHandler handler = new DefaultMeiliErrorHandler(request);
        Optional<MeiliSearchException> exception = handler.buildException(400, "indexes", Map.of(), "");

        Assertions.assertThat(exception).hasValueSatisfying(e -> Assertions.assertThat(e).isInstanceOf(MeiliSearchException.class));
    }
}
