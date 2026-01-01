package com.pascalnb.eddie.listeners;


import java.util.function.Consumer;
import java.util.function.Function;

public interface HandlerCollection<T> extends Consumer<T> {

    Function<T, String> getIdProvider();

    void addListener(String id, Consumer<T> listener);



}
