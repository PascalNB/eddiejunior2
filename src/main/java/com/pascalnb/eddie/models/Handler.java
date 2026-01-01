package com.pascalnb.eddie.models;

import net.dv8tion.jda.api.events.GenericEvent;

import java.util.function.Consumer;

public interface Handler<T extends GenericEvent> extends Consumer<T> {

    String getId();

    Class<T> getType();

}
