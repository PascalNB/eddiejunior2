package com.pascalnb.eddie.components.feedback.past;

import com.pascalnb.eddie.components.feedback.StoredSession;
import com.pascalnb.eddie.models.dynamic.UpdatingButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class FeedbackPastRemoveButton extends UpdatingButton<FeedbackPastComponent> {

    private final StoredSession session;

    public FeedbackPastRemoveButton(FeedbackPastComponent component, String id, StoredSession session) {
        super(component, id);
        this.session = session;
    }

    public static BiFunction<FeedbackPastComponent, String, FeedbackPastRemoveButton> forSession(
        StoredSession session) {
        return (component, id) -> new FeedbackPastRemoveButton(component, id, session);
    }

    @Override
    public Button getEntity() {
        return Button.danger(getId(), "Remove").withEmoji(Emoji.fromUnicode("\uD83D\uDDD1️"));
    }

    @Override
    public @Nullable FeedbackPastComponent apply(ButtonInteractionEvent event, InteractionHook hook) {
        getComponent().getParentComponent().removeSession(session);
        return getComponent().copy();
    }

}
