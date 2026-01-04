package com.pascalnb.eddie.components.event.edit;

import com.pascalnb.eddie.components.event.EventComponent;
import com.pascalnb.eddie.models.dynamic.UpdatingButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LinkDeleteButton extends UpdatingButton<LinkEditComponent> {

    public LinkDeleteButton(LinkEditComponent component, String id) {
        super(component, id);
    }

    @Override
    public Button getEntity() {
        return Button.danger(getId(), "Remove").withEmoji(Emoji.fromUnicode("\uD83D\uDDD1️"));
    }

    @Override
    public @Nullable LinkEditComponent apply(ButtonInteractionEvent event, InteractionHook hook) {
        EventComponent.Link link = getComponent().getSelectedLink();

        List<EventComponent.Link> newLinks = new ArrayList<>(getComponent().getLinks());
        newLinks.remove(link);
        List<EventComponent.Link> newChanges = new ArrayList<>(getComponent().getChanges());
        newChanges.add(link);

        return createComponent(getComponent().factory(
            newLinks,
            null,
            newChanges
        ));
    }

}
