package org.raissi.meilisearch.control;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.*;

public class TryTest {


    @Test
    public void testSuccess() {
        AtomicBoolean successCalled = new AtomicBoolean(false);
        String result = Try.of(this::alwaysSuccess)
                .andThenTry(this::checkedAlwaysSuccess)
                .ifSuccess(s -> successCalled.set(true))
                .ignoreErrors()
                .orElse("");

        assertThat(successCalled).isTrue();
        assertThat(result).isEqualTo("OK OK");
    }

    @Test
    public void shouldReturnException() {
        String exceptionMessage = Try.of(this::alwaysFailure)
                .exception()
                .map(Throwable::getMessage)
                .orElse(null);
        assertThat(exceptionMessage).isEqualTo("Failure");
    }

    private String alwaysSuccess() {
        return "OK";
    }

    private String alwaysFailure() {
        throw new RuntimeException("Failure");
    }

    private String checkedAlwaysSuccess(String s) throws Exception{
        return "OK "+s;
    }
}
