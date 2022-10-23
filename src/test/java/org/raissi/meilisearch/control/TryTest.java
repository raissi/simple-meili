package org.raissi.meilisearch.control;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TryTest {


    @Test
    public void testSuccess() {
        AtomicBoolean successCalled = new AtomicBoolean(false);
        String result = Try.of(this::alwaysSuccess)
                .andThenTry(this::checkedAlwaysSuccess)
                .ifSuccess(s -> successCalled.set(true))
                .ignoreErrors()
                .orElse("");

        Assertions.assertTrue(successCalled.get());
        Assertions.assertEquals("OK OK", result);
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
