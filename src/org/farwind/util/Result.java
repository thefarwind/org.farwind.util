package org.farwind.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

/**
 * A type for handling checked exceptions in a util manner in
 * Java. This Interface has two variants -- {@link Ok} and {@link Err}.
 * Correct Values are wrapped in the {@link Ok} variant, and any checked
 * exceptions that would be thrown should be wrapped in the {@link Err}.
 * <br>
 * <br>
 * This result, instead of the value it wraps, gets passed through the
 * chained function calls. At the end, to get at the wrapped value, the
 * error must be handled (unless explicitly told to do otherwise through
 * the unwrap method.
 * <br>
 * <br>
 * This is heavily based of the Result type in the programming language
 * rust. The function names and some parts of the comments were borrowed
 * from the documentation.
 * Created by bryan.e.barnhart on 10/26/2016.
 */
public interface Result<T, E extends Throwable> extends Iterable<T> {

    /**
     * Returns true if {@link Ok}.
     * @return true if {@link Ok} or false if {@link Err}
     */
    boolean isOk();

    /**
     * Returns false if {@link Err}.
     * @return true if {@link Err} or false if {@link Ok}
     */
    boolean isErr();

    /**
     * Returns {@link Optional} of the wrapped {@link T} value,
     * returning an empty {@link Optional} if this is an {@link Err}.
     * @return The {@link Optional} of the current {@link Result}
     */
    Optional<T> ok();

    /**
     * Returns{@link Optional} of the wrapped {@link E} value,
     * returning an empty {@link Optional} if this is an {@link Ok}
     * @return The {@link  Optional} of the current {@link Result}
     */
    Optional<E> err();

    /**
     * Maps wrapped {@link T} to new {@link Result} with wrapped
     * {@link U} using the given function if the current type is
     * {@link Ok}. Otherwise, return {@link Err} with current
     * wrapped {@link E}.
     * @param op The mapping to apply to {@link T}
     * @param <U> The wrapped type of the returned {@link Result}
     * @return The new {@link Result} after mapping {@link T}
     */
    <U> Result<U, E> map(Function<T, U> op);

    /**
     * Maps wrapped {@link E} to new {@link Result} with wrapped
     * {@link F} using the given function if the current type is
     * {@link Err}. Otherwise, return {@link Ok} with current
     * wrapped {@link T}.
     * @param op The mapping to apply to {@link E}
     * @param <F> The wrapped error of the returned {@link Result}
     * @return The new {@link Result} after mapping {@link E}
     */
    <F extends Throwable> Result<T, F> mapErr(Function<E, F> op);

    /**
     * Returns res if the calling {@link Result} is {@link Ok}. Returns
     * wrapped {@link E} if the calling {@link Result} is {@link Err}.
     * @param res The {@link Result} to compare the current
     *            {@link Result}
     * @param <U> The wrapped value of the comparing {@link Result}
     * @return The current {@link Err} or the given {@link Result}
     */
    <U> Result<U,E> and(Result<U,E> res);

    /**
     * If {@link Ok} Call op on wrapped {@link T} and return the result.
     * Otherwise return an {@link Err} of the current wrapped error.
     * @param op The function to apply to wrapped value
     * @param <U> The wrapped type of the returned {@link Result}
     * @return The new {@link Result} or the current {@link Err}
     */
    <U> Result<U,E> andThen(Function<T, Result<U,E>> op);

    /**
     * Returns res if the calling {@link Result} is {@link Err}. Returns
     * wrapped {@link T} if calling {@link Result} is {@link Ok}.
     * @param res The {@link Result} to return if {@link Err}
     * @param <F> The {@link E} of the new {@link Result}
     * @return The current {@link Ok} or the given {@link Result}
     */
    <F extends Throwable> Result<T, F> or(Result<T, F> res);

    /**
     * Returns {@link Ok} with wrapped {@link T}, or if {@link Err}
     * applies the given function to compute the {@link F} value from
     * the wrapped {@link E} value for the returned {@link Err}.
     * @param op the mapping for the returned error
     * @param <F> the returned error type
     * @return The {@link Ok} value or the computed {@link Err}
     */
    <F extends Throwable> Result<T, F> orElse(Function<E, Result<T, F>> op);

    /**
     * If {@link Ok}, return wrapped {@link T}. Otherwise, return
     * provided default value.
     * @param optb The default value
     * @return The wrapped {@link T} if {@link Ok} else provided value
     */
    T unwrapOr(T optb);

    /**
     * Return the wrapped {@link T} value if the current {@link Result}
     * is {@link Ok}. Otherwise, if {@link Err} call the provided
     * function on the error and return the output.
     * @param op The function to handle {@link Err}
     * @return The wrapped value or the computed value of the error
     */
    T unwrapOrElse(Function<E,T> op);

    /**
     * Return the wrapped {@link T} value if the current {@link Result}.
     *
     * if {@link Ok}. Otherwise, throw a {@link NullPointerException}.
     * @return the wrapped value
     */
    T unwrap();

    /**
     * Return the wrapped {@link T} value if the current {@link Result}
     * is {@link Ok}. Otherwise, throw a {@link NullPointerException}
     * with the provided message.
     * @param msg The error message to throw if {@link Err}
     * @return The wrapped value
     */
    T expect(String msg);

    /**
     * Return the wrapped {@link E} value if the current {@link Result}
     * is {@link Err}. Otherwise, throw a {@link NullPointerException}
     * with a message from the contained {@link T}.
     * @return The wrapped error
     */
    E unwrapErr();

    final class Ok<T, E extends Throwable> implements Result<T, E> {
        private final T t;

        public Ok(T t) {
            this.t = t;
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
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private boolean next = true;
                @Override
                public boolean hasNext() {
                    return this.next;
                }

                @Override
                public T next() {
                    if(this.next) {
                        this.next = false;
                        return t;
                    } else {
                        throw new NoSuchElementException();
                    }
                }
            };
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
        public T unwrap() {
            return t;
        }

        @Override
        public T expect(String msg) {
            return t;
        }

        @Override
        public E unwrapErr() {
            throw new NullPointerException(t.toString());
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Ok && ((Ok)other).t.equals(t);
        }
    }

    final class Err<T, E extends Throwable> implements Result<T, E> {
        private final E e;

        public Err(E e){
            this.e = e;
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
        public Iterator<T> iterator(){
            return new Iterator<T>(){
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public T next(){
                    throw new NoSuchElementException();
                }
            };
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
        public T unwrap() {
            throw new NullPointerException();
        }

        @Override
        public T expect(String msg) {
            throw new NullPointerException(msg);
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
