package com.pascalnb.eddie.listeners;

import com.pascalnb.eddie.Handler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

import java.util.HashMap;
import java.util.Map;

public class HandlerListener<T extends IReplyCallback> extends ListenerAdapter {

    private final Map<String, Handler<T>> handlers = new HashMap<>();

    public void addListener(String name, Handler<T> listener) {
        handlers.put(name, listener);
    }

    public void handle(String name, T event) {
        if (!event.isFromGuild() || event.getGuild() == null) {
            return;
        }

        Handler<T> handler = handlers.get(name);
        if (handler == null) {
            return;
        }
        handler.handle(event);
    }

}
