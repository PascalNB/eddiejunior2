package com.pascalnb.eddie.database;

import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public class DatabaseResult<T> {

    private final boolean success;
    private final T data;
    private final Throwable error;

    private DatabaseResult(boolean success, T data, Throwable error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T> DatabaseResult<T> success(T data) {
        return new DatabaseResult<>(true, data, null);
    }

    public static <T> DatabaseResult<T> fail(Throwable e) {
        return new DatabaseResult<>(false, null, e);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFail() {
        return !success;
    }

    public T get() {
        return data;
    }

    public Throwable getFailure() {
        return error;
    }

    public <U> DatabaseResult<U> map(Function<T, U> mapper) {
        if (isSuccess()) {
            return new DatabaseResult<>(true, mapper.apply(data), null);
        }
        return new DatabaseResult<>(false, null, error);
    }

    public DatabaseResult<T> onFailMap(Function<? super Throwable, T> orElse) {
        if (isFail()) {
            return new DatabaseResult<>(true, orElse.apply(error), null);
        }
        return new DatabaseResult<>(true, data, null);
    }

    public void onSuccess(Consumer<? super T> consumer) {
        if (isSuccess()) {
            consumer.accept(data);
        }
    }

    public void onFail(Consumer<? super Throwable> consumer) {
        if (isFail()) {
            consumer.accept(error);
        }
    }

    public void on(Consumer<? super T> success, Consumer<? super Throwable> fail) {
        if (isSuccess()) {
            success.accept(data);
        } else {
            fail.accept(error);
        }
    }

    public T getOr(Function<? super Throwable, T> orElse) {
        if (isSuccess()) {
            return data;
        }
        return orElse.apply(error);
    }

    public T getOrThrow() {
        if (isSuccess()) {
            return data;
        }
        throw new RuntimeException(error.getMessage());
    }

    public T getOrDefault(T defaultValue) {
        if (isSuccess()) {
            return data;
        }
        return defaultValue;
    }

    public void stackTrace() {
        if (isFail()) {
            error.printStackTrace();
        }
    }

}