package io.github.meilisearch.control;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

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

    @Test
    public void shouldFoldException() {
        String foldedResult = Try.of(this::alwaysFailure)
                .fold(Throwable::getMessage, Function.identity());
        assertThat(foldedResult).isEqualTo("Failure");
    }

    @Test
    public void shouldFoldSuccess() {
        String foldedResult = Try.of(this::alwaysSuccess)
                .fold(Throwable::getMessage, Function.identity());
        assertThat(foldedResult).isEqualTo("OK");
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
