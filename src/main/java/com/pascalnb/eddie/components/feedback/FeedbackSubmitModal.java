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

import java.util.List;
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
    public Modal getEntity() {
        return Modal.create(getId(), "Submit song")
            .addComponents(
                Label.of("Song link",
                    TextInput.create("url", TextInputStyle.SHORT)
                        .setRequired(true)
                        .setValue(this.url)
                        .build()
                )
            )
            .addComponents(
                getComponent().getWebsites().isEmpty()
                    ? List.of()
                    : List.of(
                        TextDisplay.ofFormat(
                            getComponent().getWebsites().size() == 1
                            ? "The following website is allowed: %s."
                                : "The following websites are allowed: %s.",
                            getComponent().getWebsites().getPrettyValues()
                        )
                    )
            )
            .build();
    }

    @Override
    public void accept(ModalInteractionEvent event) {
        String url = Objects.requireNonNull(event.getValue("url")).getAsString();
        event.deferReply(true).queue(hook ->
            getComponent().handleSubmission(event.getMember(), url).queue(
                success ->
                    hook.sendMessageEmbeds(
                        EmbedUtil.ok("Song submitted successfully!").build()
                    ).queue(),
                e -> hook.sendMessageEmbeds(
                    EmbedUtil.error(new CommandException(e)).build()
                ).queue()
            ));
    }

    public FeedbackSubmitModal withUrl(String url) {
        return new FeedbackSubmitModal(getComponent(), getId(), url);
    }

}
