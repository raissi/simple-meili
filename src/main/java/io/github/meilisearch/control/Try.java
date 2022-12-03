package io.github.meilisearch.control;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A container object which may contain a value or an Exception.<br>
 *
 * A method returning a Try object means that it may fail.<br>
 * This class is almost completely inspired by the
 * <a href="https://github.com/vavr-io/vavr/blob/master/src/main/java/io/vavr/control/Try.java">Vavr library's</a>
 * and also  <a href="https://junit.org/junit5/docs/5.4.0/api/org/junit/platform/commons/function/Try.html">JUnit's Try</a>
 * @param <T> the type of the contained value
 */
public interface Try<T> {

    static <T> Try<T> of(Callable<? extends T> callable) {
        Objects.requireNonNull(callable, "callable is null");
        try {
            return new Try.Success<>(callable.call());
        } catch (Exception exception) {
            return new Try.Failure<>(exception);
        }
    }

    static <V> Try<V> ofTry(Callable<Try<V>> action) {
        try {
            return action.call();
        }
        catch (Exception e) {
            return new Try.Failure<>(e);
        }
    }

    static <V> Try<V> failure(Exception e) {
        return new Failure<>(e);
    }

    static <V> Try<V> success(V value) {
        return new Success<>(value);
    }

    Optional<T> ignoreErrors();

    Optional<Exception> exception();

    Try<T> ifFailure(Consumer<Exception> causeConsumer);

    Try<T> ifSuccess(Consumer<T> valueConsumer);

    /**
     * If this {@code Try} is a success, apply the supplied transformer to its
     * value and return a new successful or failed {@code Try} depending on the
     * transformer's outcome; if this {@code Try} is a failure, do nothing.
     *
     * @param transformer the transformer to try; must not be {@code null}
     * @param <U> the type of the new value
     * @return a succeeded or failed {@code Try}; never {@code null}
     */
    <U> Try<U> andThenTry(Transformer<T, U> transformer);

    /**
     * If this {@code Try} is a success, apply the supplied function to its
     * value and return the resulting {@code Try}; if this {@code Try} is a
     * failure, do nothing.
     *
     * @param function the function to apply; must not be {@code null}
     * @param <U> the type of the new value
     * @return a succeeded or failed {@code Try}; never {@code null}
     */
    <U> Try<U> andThen(Function<T, Try<U>> function);

    /**
     * If this {@code Try} is a failure, call the supplied function to transform the exception and return a
     * new failed {@code Try} with the new exception;
     * if this {@code Try} is a success, do nothing.
     *
     * @param function the action to try; must not be {@code null}
     * @param <X> the type of the new Exception
     * @return a succeeded or failed {@code Try}; never {@code null}
     */
    <X extends Exception> Try<T> mapFailure(Function<Exception, X> function);

    /**
     * Folds either the {@code Failure} or the {@code Success} side of the Try value.
     *
     * @param ifFail  maps the left value if this is a {@code Failure}
     * @param f maps the value if this is a {@code Success}
     * @param <X>    type of the folded value
     * @return A value of type X
     */
    <X> X fold(Function<Exception, X> ifFail, Function<T, X> f);

    /**
     * If this {@code Try} is a failure, call the supplied action and return a
     * new successful or failed {@code Try} depending on the action's outcome;
     * if this {@code Try} is a success, do nothing.
     *
     * @param action the action to try; must not be {@code null}
     * @return a succeeded or failed {@code Try}; never {@code null}
     */
    Try<T> orElseTry(Callable<T> action);

    /**
     * If this {@code Try} is a failure, call the supplied supplier and return
     * the resulting {@code Try}; if this {@code Try} is a success, do nothing.
     *
     * @param supplier the supplier to call; must not be {@code null}
     * @return a succeeded or failed {@code Try}; never {@code null}
     */
    T orElse(Supplier<T> supplier);

    /**
     * If this {@code Try} is a failure, call the supplied mapper and throws
     * the resulting {@code X}; if this {@code Try} is a success, returns the success value.
     *
     * @param exceptionMapper the supplier to call; must not be {@code null}
     * @param <X> the type of the new Exception
     * @return the encapsulated value or throws {@code X}
     * @throws X the result of mapping the failure
     */
    <X extends Exception> T orElseThrow(Function<Exception, X> exceptionMapper) throws X;

    /**
     * A transformer for values of type {@code S} to type {@code T}.
     *
     * <p>The {@code Transformer} interface is similar to {@link Function},
     * except that a {@code Transformer} may throw an exception.
     * Copied as is from org.junit.platform.commons.function.Try.Transformer
     */
    @FunctionalInterface
    public interface Transformer<S, T> {

        /**
         * Apply this transformer to the supplied value.
         * @param value the value to be transformed
         * @return the result of transformation
         * @throws Exception if the transformation fails
         */
        T apply(S value) throws Exception;

    }
    class Success<V> implements Try<V> {
        private final V value;

        public Success(V value) {
            this.value = value;
        }

        @Override
        public Optional<V> ignoreErrors() {
            return Optional.ofNullable(this.value);
        }

        @Override
        public Optional<Exception> exception() {
            return Optional.empty();
        }

        @Override
        public Try<V> ifFailure(Consumer<Exception> causeConsumer) {
            return this;
        }

        @Override
        public Try<V> ifSuccess(Consumer<V> valueConsumer) {
            Objects.requireNonNull(valueConsumer, "valueConsumer is null");
            valueConsumer.accept(this.value);
            return this;
        }

        @Override
        public <U> Try<U> andThenTry(Transformer<V, U> transformer) {
            Objects.requireNonNull(transformer, "transformer is null");
            return Try.of(() -> transformer.apply(this.value));
        }

        @Override
        public <U> Try<U> andThen(Function<V, Try<U>> function) {
            Objects.requireNonNull(function, "function is null");
            return Try.ofTry(() -> function.apply(this.value));
        }

        @Override
        public <X extends Exception> Try<V> mapFailure(Function<Exception, X> function) {
            return this;
        }

        @Override
        public <X> X fold(Function<Exception, X> ifFail, Function<V, X> f) {
            return f.apply(this.value);
        }

        @Override
        public Try<V> orElseTry(Callable<V> action) {
            return this;
        }

        @Override
        public V orElse(Supplier<V> supplier) {
            return this.value;
        }

        @Override
        public <X extends Exception> V orElseThrow(Function<Exception, X> exceptionMapper) throws X {
            return this.value;
        }
    }

    class Failure<V> implements Try<V> {

        private final Exception cause;

        /**
         *
         * @return This failure cast as container of U.
         * @param <U> the type of success value. Not used here. Hence the cast is safe
         */
        @SuppressWarnings("unchecked")
        private <U> Failure<U> as() {
            return (Failure<U>) this;
        }

        public Failure(Exception cause) {
            this.cause = cause;
        }

        @Override
        public Optional<V> ignoreErrors() {
            return Optional.empty();
        }

        @Override
        public Optional<Exception> exception() {
            return Optional.of(cause);
        }

        @Override
        public Try<V> ifFailure(Consumer<Exception> causeConsumer) {
            Objects.requireNonNull(causeConsumer, "causeConsumer is null");
            causeConsumer.accept(this.cause);
            return this;
        }

        @Override
        public Try<V> ifSuccess(Consumer<V> valueConsumer) {
            return this;
        }

        @Override
        public <U> Try<U> andThenTry(Transformer<V, U> transformer) {
            return this.as();
        }

        @Override
        public <U> Try<U> andThen(Function<V, Try<U>> function) {
            return this.as();
        }

        @Override
        public <X extends Exception> Try<V> mapFailure(Function<Exception, X> function) {
            return new Failure<>(function.apply(this.cause));
        }

        @Override
        public <X> X fold(Function<Exception, X> ifFail, Function<V, X> f) {
            return ifFail.apply(this.cause);
        }

        @Override
        public Try<V> orElseTry(Callable<V> action) {
            Objects.requireNonNull(action, "action is null");
            return Try.of(action);
        }

        @Override
        public V orElse(Supplier<V> supplier) {
            Objects.requireNonNull(supplier, "supplier is null");
            return supplier.get();
        }

        @Override
        public <X extends Exception> V orElseThrow(Function<Exception, X> exceptionMapper) throws X {
            throw exceptionMapper.apply(this.cause);
        }
    }
}
