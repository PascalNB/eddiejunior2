package com.pascalnb.eddie.listeners;

import com.pascalnb.eddie.models.EddieStringSelector;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class StringSelectListener extends HandlerListener<StringSelectInteractionEvent>{

    public void addStringSelector(EddieStringSelector<?> stringSelector) {
        this.addListener(stringSelector.getId(), stringSelector);
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        this.handle(event.getCustomId(), event);
    }

}
