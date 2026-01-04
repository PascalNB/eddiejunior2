package com.pascalnb.eddie.listeners;

import com.pascalnb.eddie.models.EventSubscriber;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.internal.utils.ClassWalker;

import java.util.*;
import java.util.function.Function;

public class EventHandler implements EventSubscriber<GenericEvent> {

    private final String id;
    private final Map<Class<? extends GenericEvent>, HandlerCollection<GenericEvent>> listeners = new HashMap<>();

    public EventHandler(String id) {
        this.id = id;
    }

    public <T extends GenericEvent> void registerEvent(Class<T> clazz, Function<T, String> idProvider) {
        // noinspection unchecked
        listeners.put(clazz, (MapHandlerCollection<GenericEvent>) new MapHandlerCollection<>(clazz.getName(),
            idProvider));
    }

    public <T extends GenericEvent> void registerGenericEvent(Class<T> clazz) {
        listeners.put(clazz, new GenericHandlerCollection<>(clazz.getName()));
    }

    public <T extends GenericEvent> void addSubscriber(EventSubscriber<T> eventSubscriber) {
        Class<T> clazz = eventSubscriber.getEventType();
        // noinspection unchecked
        HandlerCollection<T> listener = (HandlerCollection<T>) listeners.get(clazz);
        if (listener == null) {
            throw new IllegalArgumentException("Unknown handler for type: " + clazz);
        }
        listener.addListener(eventSubscriber.getId(), eventSubscriber);
    }

    public Collection<HandlerCollection<GenericEvent>> getHandlers(Class<? extends GenericEvent> clazz) {
        List<Class<?>> classes = new ArrayList<>();
        ClassWalker.range(clazz, GenericEvent.class).forEach(classes::add);
        classes.add(GenericEvent.class);

        //noinspection SuspiciousMethodCalls
        return classes.stream()
            .map(this.listeners::get)
            .filter(Objects::nonNull)
            .toList();
    }

    @Override
    public void accept(GenericEvent event) {
        getHandlers(event.getClass()).forEach(listener -> listener.accept(event));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Class<GenericEvent> getEventType() {
        return GenericEvent.class;
    }

}
