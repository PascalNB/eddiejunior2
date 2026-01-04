package com.pascalnb.eddie.components.event.edit.session;

import com.pascalnb.eddie.components.event.EventComponent;
import com.pascalnb.eddie.models.dynamic.UpdatingButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class SessionDeleteButton extends UpdatingButton<SessionComponent> {

    public SessionDeleteButton(SessionComponent component, String id) {
        super(component, id);
    }

    @Override
    public Button getEntity() {
        return Button.danger(getId(), "Remove").withEmoji(Emoji.fromUnicode("\uD83D\uDDD1️"));
    }

    @Override
    public @Nullable SessionComponent apply(ButtonInteractionEvent event, InteractionHook hook) {
        EventComponent.Link link = getComponent().getLink();
        EventComponent.Session selectedSession = getComponent().getSelectedSession();
        EventComponent.Session newSession = new EventComponent.Session(selectedSession.channel(), null);
        Collection<EventComponent.Session> sessions = new ArrayList<>(link.sessions());
        sessions.remove(selectedSession);
        sessions.add(newSession);
        EventComponent.Link newLink = new EventComponent.Link(link.name(), link.keywords(), link.components(), sessions);
        return createComponent(getComponent().factory(newLink, newSession));
    }

}
