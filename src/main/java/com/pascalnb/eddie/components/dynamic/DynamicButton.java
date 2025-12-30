package com.pascalnb.eddie.components.dynamic;

import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;

public abstract class DynamicButton<T extends DynamicComponent<T>> extends EddieButton<T> {

    public DynamicButton(T component, String id) {
        super(component, id);
    }

    public T createComponent(DynamicComponentFactory<T> factory) {
        return getComponent().createDynamicComponent(factory);
    }

    @Override
    public void handle(ButtonInteractionEvent event) {
            T t = apply(event);
            if (t != null) {
                event.editMessage(MessageEditData.fromCreateData(t.getMessage()))
                    .useComponentsV2(t.useComponentsV2())
                    .queue();
            }
    }

    public abstract @Nullable T apply(ButtonInteractionEvent event);

}
