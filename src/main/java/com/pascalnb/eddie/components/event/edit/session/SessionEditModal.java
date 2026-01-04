package com.pascalnb.eddie.components.event.edit.session;

import com.pascalnb.eddie.components.event.EventComponent;
import com.pascalnb.eddie.models.dynamic.UpdatingModal;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.modals.Modal;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SessionEditModal extends UpdatingModal<SessionComponent> {

    public SessionEditModal(SessionComponent component, String id) {
        super(component, id);
    }

    @Override
    public Modal getEntity() {
        EventComponent.Session session = getComponent().getSelectedSession();
        return Modal.create(getId(), "Edit message")
            .addComponents(
                TextDisplay.ofFormat("Edit the message that will be sent to %s.", session.channel().getAsMention()),
                Label.of(
                    "Message",
                    "Leave empty to delete the message",
                    TextInput.create("message", TextInputStyle.PARAGRAPH)
                        .setValue(session.message())
                        .setRequired(false)
                        .setMaxLength(TextInput.MAX_VALUE_LENGTH)
                        .build()
                )
            )
            .build();
    }

    @Override
    public @Nullable SessionComponent apply(ModalInteractionEvent event, InteractionHook hook) {
        String message = Objects.requireNonNull(event.getValue("message")).getAsString();

        if (message.isBlank()) {
            message = null;
        }

        EventComponent.Session newSession = new EventComponent.Session(getComponent().getSelectedSession().channel(), message);

        EventComponent.Link currentLink = getComponent().getLink();
        Collection<EventComponent.Session> sessions = new ArrayList<>(currentLink.sessions());
        sessions.remove(getComponent().getSelectedSession());
        sessions.add(newSession);

        EventComponent.Link newLink = new EventComponent.Link(currentLink.name(), currentLink.keywords(), currentLink.components(), sessions);

        return createComponent(getComponent().factory(
            newLink,
            newSession
        ));
    }

}
