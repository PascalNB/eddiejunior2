package com.pascalnb.eddie.components.dynamic;

import com.pascalnb.eddie.listeners.ButtonListener;
import com.pascalnb.eddie.listeners.ModalListener;
import com.pascalnb.eddie.listeners.StringSelectListener;
import com.pascalnb.eddie.models.EddieButton;
import com.pascalnb.eddie.models.EddieComponent;
import com.pascalnb.eddie.models.EddieModal;
import com.pascalnb.eddie.models.EddieStringSelector;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DynamicListener extends ListenerAdapter {

    private final String id;
    private final Map<String, DynamicChildComponent> children = new HashMap<>();
    private long index = 0;

    public DynamicListener(String id) {
        this.id = id;
    }

    public DynamicListenerChild createInstance() {
        String childId = createChildId();
        DynamicChildComponent child = new DynamicChildComponent(this, childId);
        this.children.put(childId, child);
        return child;
    }

    private String createChildId() {
        return this.id + "_" + this.index++;
    }

    private String createEntityId(String childId, String suffix) {
        String newId = childId + "_" + suffix;
        if (newId.length() > Button.ID_MAX_LENGTH) {
            throw new IllegalArgumentException("Button id too long");
        }
        return newId;
    }

    public void deleteInstance(String childId) {
        children.remove(childId);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        DynamicChildComponent component = getChildForInteractionId(event.getCustomId());
        if (component == null) {
            return;
        }
        component.getButtonListener().onButtonInteraction(event);
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        DynamicChildComponent component = getChildForInteractionId(event.getCustomId());
        if (component == null) {
            return;
        }
        component.getStringSelectListener().onStringSelectInteraction(event);
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        DynamicChildComponent component = getChildForInteractionId(event.getCustomId());
        if (component == null) {
            return;
        }
        component.getModalListener().onModalInteraction(event);
    }

    private @Nullable DynamicChildComponent getChildForInteractionId(String interactionId) {
        if (!interactionId.startsWith(this.id)) {
            return null;
        }
        String[] splitId = interactionId.split("_", 3);
        if (splitId.length != 3) {
            return null;
        }
        return this.children.get(this.id + "_" + splitId[1]);
    }

    private static class DynamicChildComponent implements DynamicListenerChild {

        private final DynamicListener dynamicListener;
        private final String id;
        private final ButtonListener buttonListener = new ButtonListener();
        private final StringSelectListener stringSelectListener = new StringSelectListener();
        private final ModalListener modalListener = new ModalListener();

        public DynamicChildComponent(DynamicListener dynamicListener, String id) {
            this.dynamicListener = dynamicListener;
            this.id = id;
        }

        public void addButton(EddieButton<?> button) {
            buttonListener.addButton(button);
        }

        public void addStringSelector(EddieStringSelector<?> stringSelector) {
            stringSelectListener.addStringSelector(stringSelector);
        }

        public void addModal(EddieModal<?> modal) {
            modalListener.addModal(modal);
        }

        public ButtonListener getButtonListener() {
            return buttonListener;
        }

        public StringSelectListener getStringSelectListener() {
            return stringSelectListener;
        }

        public ModalListener getModalListener() {
            return modalListener;
        }

        @Override
        public <C extends EddieComponent, T extends EddieButton<C>> T createDynamicButton(String customId,
            Function<String, T> provider) {
            String entityId = dynamicListener.createEntityId(id, customId);
            T t = provider.apply(entityId);
            addButton(t);
            return t;
        }

        @Override
        public <C extends EddieComponent, T extends EddieStringSelector<C>> T createDynamicStringSelector(
            String customId, Function<String, T> provider) {
            String entityId = dynamicListener.createEntityId(id, customId);
            T t = provider.apply(entityId);
            addStringSelector(t);
            return t;
        }

        @Override
        public <C extends EddieComponent, T extends EddieModal<C>> T createDynamicModal(
            String customId, Function<String, T> provider) {
            String entityId = dynamicListener.createEntityId(id, customId);
            T t = provider.apply(entityId);
            addModal(t);
            return t;
        }

        @Override
        public String getId() {
            return id;
        }

    }

}
