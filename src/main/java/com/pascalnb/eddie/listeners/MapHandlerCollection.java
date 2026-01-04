package com.pascalnb.eddie.listeners;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class MapHandlerCollection<T> implements HandlerCollection<T> {

    private final String id;
    private final Function<T, String> idProvider;
    private final Map<String, Consumer<T>> listeners = new HashMap<>();

    public MapHandlerCollection(String id, Function<T, String> idProvider) {
        this.idProvider = idProvider;
        this.id = id;
    }

    @Override
    public void addListener(String id, Consumer<T> listener) {
        listeners.put(id, listener);
    }

    @Override
    public void accept(T event) {
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

    @Override
    public String getId() {
        return id;
    }

}
