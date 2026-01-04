package com.pascalnb.eddie.components.event.edit;

import com.pascalnb.eddie.ColorUtil;
import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

public class LinkCancelButton extends EddieButton<LinkEditComponent> {

    public LinkCancelButton(LinkEditComponent component, String id) {
        super(component, id);
    }

    @Override
    public Button getEntity() {
        return Button.danger(getId(), "Cancel").withEmoji(Emoji.fromUnicode("✖️"));
    }

    @Override
    public void accept(ButtonInteractionEvent event) {
        event.editMessage(new MessageEditBuilder()
            .useComponentsV2()
            .setComponents(Container.of(
                TextDisplay.of("Edit cancelled")
            ).withAccentColor(ColorUtil.RED))
            .build()
        ).useComponentsV2().queue(callback -> getComponent().unmount());
    }

}
