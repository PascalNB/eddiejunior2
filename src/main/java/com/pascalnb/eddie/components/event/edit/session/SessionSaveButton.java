package com.pascalnb.eddie.components.event.edit.session;

import com.pascalnb.eddie.Util;
import com.pascalnb.eddie.components.event.EventComponent;
import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.ArrayList;
import java.util.Collection;

public class SessionSaveButton extends EddieButton<SessionComponent> {

    public SessionSaveButton(SessionComponent component, String id) {
        super(component, id);
    }

    @Override
    public Button getEntity() {
        return Button.success(getId(), "Submit").withEmoji(Emoji.fromUnicode("✔️"));
    }

    @Override
    public void accept(ButtonInteractionEvent event) {
        Collection<EventComponent.Link> links = new ArrayList<>(getComponent().getParentComponent().getLinks());
        EventComponent.Link currentLink = getComponent().getParentComponent().getSelectedLink();
        EventComponent.Link newLink = getComponent().getLink();

        links.remove(currentLink);
        links.add(newLink);

        event.editMessage(MessageEditData.fromCreateData(
            getComponent().getParentComponent().createComponent(
                getComponent().getParentComponent().factory(
                    links,
                    newLink,
                    Util.spread(getComponent().getParentComponent().getChanges(), newLink)
                )
            ).getMessage()
        )).useComponentsV2().queue(callback -> getComponent().unmount());
    }

}
