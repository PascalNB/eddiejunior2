package com.pascalnb.eddie.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public class GenericHandlerCollection<T> implements HandlerCollection<T> {

    private final Collection<Consumer<T>> handlers = new ArrayList<>();

    @Override
    public Function<T, String> getIdProvider() {
        return null;
    }

    @Override
    public void addListener(String id, Consumer<T> listener) {
        this.handlers.add(listener);
    }

    @Override
    public void accept(T t) {
        handlers.forEach(handler -> handler.accept(t));
    }

}
