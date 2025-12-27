package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;

public class FeedbackSubmitButton extends EddieButton<FeedbackComponent> {

    public FeedbackSubmitButton(FeedbackComponent component) {
        super(component, "feedback-submit", "Submit song");
    }

    @Override
    public Button getButton() {
        return Button.success(getId(), getLabel()).withEmoji(Emoji.fromUnicode("🎵"));
    }

    @Override
    public void handle(ButtonInteractionEvent event) {
        try {
            String submissionUrl = getComponent().getSubmission(event.getMember());
            if (submissionUrl != null) {
                event.replyModal(getEditModal(submissionUrl)).queue();
            } else {
                event.replyModal(getComponent().getSubmitModal().getModal()).queue();
            }
        } catch (CommandException e) {
            event.replyEmbeds(
                EmbedUtil.error().setDescription(e.getPrettyError()).build()
            ).setEphemeral(true).queue();
        }
    }

    private Modal getEditModal(String url) {
        return getComponent().getSubmitModal().withUrl(url).getModal();
    }

}
