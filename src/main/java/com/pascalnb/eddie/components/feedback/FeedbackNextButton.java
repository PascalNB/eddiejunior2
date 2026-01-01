package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class FeedbackNextButton extends EddieButton<FeedbackComponent> {

    public FeedbackNextButton(FeedbackComponent component) {
        super(component, "feedback-next");
    }

    @Override
    public Button getEntity() {
        return Button.primary(getId(), "Get next song").withEmoji(Emoji.fromUnicode("🎵"));
    }

    @Override
    public void accept(ButtonInteractionEvent event) {
        event.deferEdit().queue(hook -> {
            try {
                getComponent().handleNextSubmission(event.getMessage(), hook);
            } catch (CommandException e) {
                hook.setEphemeral(true).sendMessageEmbeds(
                    EmbedUtil.error(e).build()
                ).queue();
            }
        });
    }

}
