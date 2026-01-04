package com.pascalnb.eddie.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class GenericHandlerCollection<T> implements HandlerCollection<T> {

    private final String id;
    private final Map<String, Consumer<T>> handlers = new HashMap<>();

    public GenericHandlerCollection(String id) {
        this.id = id;
    }

    @Override
    public Function<T, String> getIdProvider() {
        return null;
    }

    @Override
    public void addListener(String id, Consumer<T> listener) {
        this.handlers.put(id, listener);
    }

    @Override
    public void accept(T t) {
        handlers.values().forEach(handler -> handler.accept(t));
    }

    @Override
    public String getId() {
        return id;
    }

}
