package com.pascalnb.eddie.listeners;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class MapHandlerCollection<T> implements HandlerCollection<T> {

    private final @Nullable Function<T, String> idProvider;
    private final Map<String, Consumer<T>> listeners = new HashMap<>();

    public MapHandlerCollection(@Nullable Function<T, String> idProvider) {
        this.idProvider = idProvider;
    }

    @Override
    public void addListener(String id, Consumer<T> listener) {
        listeners.put(id, listener);
    }

    @Override
    public void accept(T event) {
        if (idProvider == null) {
            listeners.values().forEach(handler -> handler.accept(event));
            return;
        }
        Consumer<T> listener = listeners.get(idProvider.apply(event));
        if (listener == null) {
            return;
        }
        listener.accept(event);
    }

    @Nullable
    public Function<T, String> getIdProvider() {
        return idProvider;
    }

}
