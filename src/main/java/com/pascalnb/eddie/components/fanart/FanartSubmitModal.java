package com.pascalnb.eddie.components.fanart;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.EddieModal;
import net.dv8tion.jda.api.components.attachmentupload.AttachmentUpload;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.List;
import java.util.Objects;

public class FanartSubmitModal extends EddieModal<FanartComponent> {

    public FanartSubmitModal(FanartComponent component) {
        super(component, "fanart-submit");
    }

    @Override
    public Modal getModal() {
        return Modal.create(getId(), "Submit fanart")
            .addComponents(
                Label.of("Title", "Brief title of your post",
                    TextInput.create("title", TextInputStyle.SHORT)
                        .setRequired(true)
                        .setRequiredRange(10, 90)
                        .build()
                ),
                Label.of("Description (optional)", "Describe your art",
                    TextInput.create("description", TextInputStyle.PARAGRAPH)
                        .setRequired(false)
                        .setRequiredRange(0, 500)
                        .build()
                ),
                Label.of("Art", "Attach your artwork(s)",
                    AttachmentUpload.create("attachments")
                        .setRequired(true)
                        .setMinValues(1)
                        .setMaxValues(4)
                        .build()
                )
            )
            .build();
    }

    @Override
    public void handle(ModalInteractionEvent event) {
        String title = Objects.requireNonNull(event.getValue("title")).getAsString();
        ModalMapping descriptionMapping = event.getValue("description");
        String description;
        if (descriptionMapping == null) {
            description = null;
        } else {
            description = descriptionMapping.getAsString();
            if (description.isBlank()) {
                description = null;
            }
        }
        List<Message.Attachment> attachments = Objects.requireNonNull(
            event.getValue("attachments")).getAsAttachmentList();

        try {
            RestAction<Message> createAction = getComponent().createSubmission(
                event.getMember(), title, description, attachments);
            event.deferReply(true).queue(hook -> createAction.queue(message -> {
                    getComponent().getLogger().info(event.getUser(), "Created submission %s", message.getJumpUrl());
                    hook.sendMessageEmbeds(EmbedUtil.ok("Submission created").build()).queue();
                }
            ));
        } catch (CommandException e) {
            event.replyEmbeds(EmbedUtil.error(e).build()).setEphemeral(true).queue();
        }
    }

}
