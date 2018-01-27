package org.farwind.util;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A type for handling checked exceptions in a util manner in
 * Java. This sealed class has two variants -- {@link Ok} and {@link Err}.
 * Correct Values are wrapped in the {@link Ok} variant, and any checked
 * exceptions that would be thrown should be wrapped in the {@link Err}.
 *
 * <p>This result, instead of the value it wraps, gets passed through the
 * chained function calls. At the end, to get at the wrapped value, the
 * error must be handled (unless explicitly told to do otherwise through
 * the unwrap method.
 *
 * <p>This is heavily based of the Result type in the programming language
 * rust. The function names and some parts of the comments were borrowed
 * from the documentation.
 * Created by bryan.e.barnhart on 10/26/2016.
 */
public abstract class Result<T, E extends Throwable> {
    private Result(){ /* prevent instantiation */ };

    /**
     * Returns {@code true} if this is an {@link Ok}, otherwise
     * {@code false}.
     *
     * @return true if {@link Ok} or false if {@link Err}
     */
    public abstract boolean isOk();

    /**
     * Returns {@code true} if this is an {@link Err}, otherwise
     * {@code false}.
     *
     * @return true if {@link Err} or false if {@link Ok}
     */
    public abstract boolean isErr();

    /**
     * Returns an {@link Optional} of the contained value if this is an
     * {@link Ok}, otherwise {@link Optional#EMPTY}.
     *
     * @return The {@link Optional} of the current {@link Result}
     */
    public abstract Optional<T> ok();

    /**
     * Returns an {@link Optional} of the contained error if this is an
     * {@link Err}, otherwise {@link Optional#EMPTY}.
     *
     * @return The {@link  Optional} of the current {@link Result}
     */
    public abstract Optional<E> err();

    /**
     * If this is an {@link Ok}, uses the provided mapping to map
     * the contained value to a new value, otherwise return an
     * {@link Err} containing the current error.
     *
     * @param op The mapping to apply to the contained value
     * @param <U> The type of the contained value after mapping
     * @return The new {@link Result} after mapping the contained value
     */
    public abstract <U> Result<U, E> map(Function<T, U> op);

    /**
     * If this is an {@link Err}, uses the provided mapping to map
     * the contained error to a new error, otherwise return an
     * {@link Ok} containing the current value.
     *
     * @param op The mapping to apply to the contained error
     * @param <F> The error type of the returned {@link Result}
     * @return The new {@link Result} after mapping the contained error
     */
    public abstract <F extends Throwable> Result<T, F> mapErr(Function<E, F> op);

    /**
     * If {@link Ok}, returns a stream containing the value, otherwise
     * return an empty {@link Stream}.
     *
     * @return The Stream containing the contained value.
     */
    public abstract Stream<T> stream();

    /**
     * If {@link Ok}, returns the provided {@code Result}, otherwise
     * returns an {@link Err} containing the current error.
     *
     * @param res The {@code Result} to compare to this {@link Result}
     * @param <U> The value type of the provided {@link Result}
     * @return The current {@link Err} or the provided {@link Result}
     */
    public abstract <U> Result<U,E> and(Result<U,E> res);

    /**
     * If {@link Ok}, maps the contained value to a {@code Result} and
     * returns that result, otherwise returns an {@link Err} containing
     * the current error.
     *
     * @param op The function to apply to wrapped value
     * @param <U> The value type of the returned {@link Result}
     * @return The computed {@link Result} or the current {@link Err}
     */
    public abstract <U> Result<U,E> andThen(Function<T, Result<U,E>> op);

    /**
     * If {@link Err}, returns the provided {@link Result}, otherwise
     * returns an {@link Ok} containing the current value.
     *
     * @param res The {@link Result} to return if {@link Err}
     * @param <F> The error type of the provided {@link Result}
     * @return The current {@link Ok} or the provided {@link Result}
     */
    public abstract <F extends Throwable> Result<T, F> or(Result<T, F> res);

    /**
     * If {@link Err}, maps the contained error to a {@code Result} and
     * returns that {@code Result}, otherwise returns an {@link Ok}
     * containing the current value.
     *
     * @param op the mapping for the returned error
     * @param <F> the error type of the returned {@link Result}
     * @return The {@link Ok} value or the computed {@link Err}
     */
    public abstract <F extends Throwable> Result<T, F> orElse(Function<E, Result<T, F>> op);

    /**
     * If {@link Ok}, returns the contained value, otherwise returns the
     * provided default value.
     *
     * @param optb The default value
     * @return The contained value if {@link Ok} else the default value
     */
    public abstract T unwrapOr(T optb);

    /**
     * If {@link Ok}, returns the contained value, otherwise returns the
     * mapped value of the current error.
     *
     * @param op The function to handle {@link Err}
     * @return The wrapped value or the computed value of the error
     */
    public abstract T unwrapOrElse(Function<E,T> op);

    /**
     * If {@link Ok}, returns the contained value, otherwise throws the
     * current error. This method provides a transition from the stream
     * and functional exception pattern back to the imperative exception
     * handling pattern.
     *
     * @return The contained value if {@link Ok}
     * @throws E The contained error if {@link Err}.
     */
    abstract T unwrapOrThrow() throws E;

    /**
     * if {@link Ok}, returns the contained value, otherwise throws a
     * {@link NoSuchElementException}.
     *
     * @return the contained value
     * @throws NoSuchElementException if {@link Err}
     */
    public abstract T unwrap();

    /**
     * if {@link Ok}, returns the contained value, otherwise throws a
     * {@link NoSuchElementException} with the provided message.
     *
     * @param msg The error message to throw if {@link Err}
     * @return The contained value
     * @throws NoSuchElementException if {@link Err}
     */
    public abstract T expect(String msg);

    /**
     * if {@link Err}, returns the contained error, otherwise throws a
     * {@link NoSuchElementException}.
     *
     * @return The contained error
     * @throws NoSuchElementException if {@link Ok}
     */
    public abstract E unwrapErr();

    public final static class Ok<T, E extends Throwable> extends Result<T, E> {
        private final T t;

        private Ok(T t) {
            this.t = t;
        }

        public static <T, E extends Throwable> Result<T, E> of(T t) {
            return new Ok<>(t);
        }

        @Override
        public boolean isOk() {
            return true;
        }

        @Override
        public boolean isErr() {
            return false;
        }

        @Override
        public Optional<T> ok() {
            return Optional.of(t);
        }

        @Override
        public Optional<E> err() {
            return Optional.empty();
        }

        @Override
        public <U> Result<U, E> map(Function<T, U> op) {
            return new Ok<>(op.apply(t));
        }

        @Override
        public <F extends Throwable> Result<T, F> mapErr(Function<E, F> op) {
            return new Ok<>(t);
        }

        @Override
        public Stream<T> stream() {
            return Stream.of(t);
        }

        @Override
        public <U> Result<U,E> and(Result<U,E> res) {
            return res;
        }

        @Override
        public <U> Result<U,E> andThen(Function<T, Result<U,E>> op) {
            return op.apply(t);
        }

        @Override
        public <F extends Throwable> Result<T, F> or(Result<T, F> res) {
            return new Ok<>(t);
        }

        @Override
        public <F extends Throwable> Result<T, F> orElse(Function<E, Result<T, F>> op) {
            return new Ok<>(t);
        }

        @Override
        public T unwrapOr(T optb) {
            return t;
        }

        @Override
        public T unwrapOrElse(Function<E,T> op) {
            return t;
        }

        @Override
        public T unwrapOrThrow() throws E {
            return t;
        }

        @Override
        public T unwrap() {
            return t;
        }

        @Override
        public T expect(String msg) {
            return t;
        }

        @Override
        public E unwrapErr() {
            throw new NoSuchElementException(t.toString());
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Ok && ((Ok)other).t.equals(t);
        }
    }

    public final static class Err<T, E extends Throwable> extends Result<T, E> {
        private final E e;

        private Err(E e){
            this.e = e;
        }

        public static <T, E extends Throwable> Result<T, E> of(E e) {
            return new Err<>(e);
        }

        @Override
        public boolean isOk() {
            return false;
        }

        @Override
        public boolean isErr() {
            return true;
        }

        @Override
        public Optional<T> ok() {
            return Optional.empty();
        }

        @Override
        public Optional<E> err() {
            return Optional.of(e);
        }

        @Override
        public <U> Result<U, E> map(Function<T, U> op) {
            return new Err<>(e);
        }

        @Override
        public <F extends Throwable> Result<T, F> mapErr(Function<E, F> op) {
            return new Err<>(op.apply(e));
        }

        @Override
        public Stream<T> stream() {
            return Stream.empty();
        }

        @Override
        public <U> Result<U,E> and(Result<U,E> res) {
            return new Err<>(e);
        }

        @Override
        public <U> Result<U,E> andThen(Function<T, Result<U,E>> op) {
            return new Err<>(e);
        }

        @Override
        public <F extends Throwable> Result<T, F> or(Result<T, F> res) {
            return res;
        }

        @Override
        public <F extends Throwable> Result<T, F> orElse(Function<E, Result<T, F>> op) {
            return op.apply(e);
        }

        @Override
        public T unwrapOr(T optb) {
            return optb;
        }

        @Override
        public T unwrapOrElse(Function<E,T> op) {
            return op.apply(e);
        }

        @Override
        public T unwrapOrThrow() throws E {
            throw e;
        }

        @Override
        public T unwrap() {
            throw new NoSuchElementException("No value present");
        }

        @Override
        public T expect(String msg) {
            throw new NoSuchElementException(msg);
        }

        @Override
        public E unwrapErr() {
            return e;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Err && ((Err)other).e.equals(e);
        }
    }
}
