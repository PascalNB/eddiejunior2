package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.EddieModal;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;

import java.util.Objects;

public class FeedbackSubmitModal extends EddieModal<FeedbackComponent> {

    private final String url;

    public FeedbackSubmitModal(FeedbackComponent component) {
        this(component, "feedback-submit", null);
    }

    private FeedbackSubmitModal(FeedbackComponent component, String name, String url) {
        super(component, name);
        this.url = url;
    }

    @Override
    public Modal getModal() {
        return Modal.create(getId(), "Submit song")
            .addComponents(
                Label.of("Song link",
                    TextInput.create("url", TextInputStyle.SHORT)
                        .setRequired(true)
                        .setValue(this.url)
                        .build()
                ),
                TextDisplay.ofFormat(
                    "The following websites are allowed: %s.",
                    getComponent().getWebsites().getPrettyValues()
                )
            )
            .build();
    }

    @Override
    public void handle(ModalInteractionEvent event) {
        String url = Objects.requireNonNull(event.getValue("url")).getAsString();
        event.deferReply(true).queue(hook -> {
            try {
                getComponent().handleSubmission(event.getMember(), url);
                getComponent().getLogger().info(event.getUser(), "Submitted song <%s>.", url);
                hook.sendMessageEmbeds(
                    EmbedUtil.ok("Song submitted successfully!").build()
                ).queue();
            } catch (CommandException e) {
                hook.sendMessageEmbeds(
                    EmbedUtil.error(e).build()
                ).queue();
            }
        });
    }

    public FeedbackSubmitModal withUrl(String url) {
        return new FeedbackSubmitModal(getComponent(), getId(), url);
    }

}
