package com.pascalnb.eddie.models.dynamic;

import com.pascalnb.eddie.models.EddieComponent;
import com.pascalnb.eddie.models.EddieSubscriberSubcomponent;
import com.pascalnb.eddie.models.EventSubscriber;
import com.pascalnb.eddie.listeners.JDAEventHandler;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class DynamicSubcomponent<T extends EddieComponent> extends EddieSubscriberSubcomponent<GenericEvent, T> {

    private final JDAEventHandler jdaEventHandlerListener;
    private final Map<String, Child> children = new HashMap<>();
    private long index = 0;

    public DynamicSubcomponent(T component, String id) {
        super(component, id);
        this.jdaEventHandlerListener = new JDAEventHandler(getId());
    }

    public <R> R createInstance(Function<DynamicRegister, R> provider) {
        return provider.apply(createInstance());
    }

    public DynamicRegister createInstance() {
        String childId = createChildId();
        Child child = new Child(childId);
        this.children.put(childId, child);
        return child;
    }

    private String createChildId() {
        return getId() + "_" + this.index++;
    }

    private String createEntityId(String childId, String suffix) {
        String newId = childId + " " + suffix;
        if (newId.length() > 100) {
            throw new IllegalArgumentException("Entity ID too long");
        }
        return newId;
    }

    public void removeInstance(String childId) {
        children.remove(childId);
    }

    @Override
    public void accept(GenericEvent event) {
        jdaEventHandlerListener.getHandlers(event.getClass()).forEach(listener -> {
            if (listener.getIdProvider() == null) {
                return;
            }
            String interactionId = listener.getIdProvider().apply(event);
            Child child = getChildForInteractionId(interactionId);
            if (child != null) {
                child.accept(event);
            }
        });
    }

    private @Nullable Child getChildForInteractionId(String interactionId) {
        if (!interactionId.contains(getId()) || !interactionId.contains(" ")) {
            return null;
        }

        String[] splitId = interactionId.split(" ");

        for (int i = 1; i < splitId.length; i++) {
            String[] splitSec = Arrays.copyOfRange(splitId, 0, i);
            String sec = String.join(" ", splitSec);

            if (!sec.startsWith(getId())) {
                continue;
            }

            int splitIndex = sec.lastIndexOf("_");
            if (splitIndex == -1) {
                return null;
            }

            String id = sec.substring(0, splitIndex);
            if (!getId().equals(id)) {
                return null;
            }

            return this.children.get(sec);
        }

        return null;
    }

    @Override
    public Class<GenericEvent> getEventType() {
        return GenericEvent.class;
    }

    private class Child extends JDAEventHandler implements DynamicRegister {

        public Child(String id) {
            super(id);
        }

        @Override
        public <U extends GenericEvent, R extends EventSubscriber<U>> R registerDynamic(String customId,
            Function<String, R> provider) {
            String entityId = createEntityId(getId(), customId);
            R subscriber = provider.apply(entityId);
            return registerDynamic(subscriber);
        }

        @Override
        public <U extends GenericEvent, R extends EventSubscriber<U>> R registerDynamic(Supplier<R> provider) {
            return registerDynamic(provider.get());
        }

        @Override
        public <U extends GenericEvent, R extends EventSubscriber<U>> R registerDynamic(R subscriber) {
            addSubscriber(subscriber);
            return subscriber;
        }

        @Override
        public String getDynamicId() {
            return getId();
        }

        @Override
        public void unmount() {
            removeInstance(getDynamicId());
        }

    }

}
