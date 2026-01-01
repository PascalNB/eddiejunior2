package com.pascalnb.eddie.models.dynamic;

import com.pascalnb.eddie.models.EddieComponent;
import com.pascalnb.eddie.models.EntityComponentHandler;
import com.pascalnb.eddie.models.Handler;
import com.pascalnb.eddie.listeners.JDAEventHandler;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DynamicListener extends EntityComponentHandler<Void, GenericEvent, EddieComponent> {

    private final JDAEventHandler jdaEventHandlerListener;
    private final Map<String, DynamicChildComponent> children = new HashMap<>();
    private long index = 0;

    public DynamicListener( String id) {
        super(null, id);
        this.jdaEventHandlerListener = new JDAEventHandler(null);
    }

    public DynamicListenerChild createInstance() {
        String childId = createChildId();
        DynamicChildComponent child = new DynamicChildComponent(this, childId);
        this.children.put(childId, child);
        return child;
    }

    private String createChildId() {
        return getId() + "_" + this.index++;
    }

    private String createEntityId(String childId, String suffix) {
        String newId = childId + "_" + suffix;
        if (newId.length() > Button.ID_MAX_LENGTH) {
            throw new IllegalArgumentException("Button id too long");
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
            DynamicChildComponent child = getChildForInteractionId(listener.getIdProvider().apply(event));
            if (child != null) {
                child.accept(event);
            }
        });
    }

    @Override
    public Class<GenericEvent> getType() {
        return GenericEvent.class;
    }

    private @Nullable DynamicChildComponent getChildForInteractionId(String interactionId) {
        if (!interactionId.startsWith(getId())) {
            return null;
        }
        String[] splitId = interactionId.split("_", 3);
        if (splitId.length != 3) {
            return null;
        }
        return this.children.get(getId() + "_" + splitId[1]);
    }

    @Override
    public Void getEntity() {
        return null;
    }

    @Override
    public Class<Void> getEntityType() {
        return Void.class;
    }

    private static class DynamicChildComponent extends JDAEventHandler implements DynamicListenerChild {

        private final DynamicListener dynamicListener;

        public DynamicChildComponent(DynamicListener dynamicListener, String id) {
            super(id);
            this.dynamicListener = dynamicListener;
        }

        @Override
        public <T extends GenericEvent, R extends Handler<T>> R createDynamic(String customId,
            Function<String, R> provider) {
            String entityId = dynamicListener.createEntityId(getId(), customId);
            R handler = provider.apply(entityId);
            addHandler(handler);
            return handler;
        }

        @Override
        public String getDynamicId() {
            return getId();
        }

    }

}
