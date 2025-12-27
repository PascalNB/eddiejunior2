package com.pascalnb.eddie;

public interface Handler<T> {

    void handle(T event);

}
