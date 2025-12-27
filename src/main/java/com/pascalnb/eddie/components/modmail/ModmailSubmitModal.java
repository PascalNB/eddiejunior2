package com.pascalnb.eddie.components.modmail;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.EddieModal;
import net.dv8tion.jda.api.components.attachmentupload.AttachmentUpload;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.requests.RestAction;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class ModmailSubmitModal extends EddieModal<ModmailComponent> {

    public ModmailSubmitModal(ModmailComponent component) {
        super(component, "modmail-submit");
    }

    @Override
    public Modal getModal() {
        return Modal.create(getId(), "Create modmail ticket")
            .addComponents(
                Label.of("Title", "Brief title of your topic",
                    TextInput.create("title", TextInputStyle.SHORT)
                        .setRequired(true)
                        .setRequiredRange(10, 90)
                        .build()
                ),
                Label.of("Message", "Describe your problem here",
                    TextInput.create("message", TextInputStyle.PARAGRAPH)
                        .setRequired(true)
                        .setRequiredRange(20, 4000)
                        .build()
                ),
                Label.of("Screenshots", "Attach screenshots if necessary",
                    AttachmentUpload.create("attachments")
                        .setRequired(false)
                        .setMinValues(0)
                        .setMaxValues(4)
                        .build()
                )
            )
            .build();
    }

    @Override
    public void handle(ModalInteractionEvent event) {
        String title = Objects.requireNonNull(event.getValue("title")).getAsString();
        String message = Objects.requireNonNull(event.getValue("message")).getAsString();
        ModalMapping attachmentsMapping = event.getValue("attachments");
        List<Message.Attachment> attachments;
        if (attachmentsMapping == null) {
            attachments = List.of();
        } else {
            attachments = attachmentsMapping.getAsAttachmentList();
        }

        try {
            RestAction<ThreadChannel> createAction = getComponent().createTicket(
                event.getMember(), title, message, attachments);
            event.deferReply(true).queue(hook -> createAction.queue(thread ->
                hook.sendMessageComponents(
                    Container.of(
                        Section.of(
                            Button.link(thread.getJumpUrl(), "Go to ticket"),
                            TextDisplay.of("✅ Ticket created")
                        )
                    )
                ).useComponentsV2().queue()
            ));
        } catch (CommandException e) {
            event.replyEmbeds(EmbedUtil.error(e).build()).setEphemeral(true).queue();
        }
    }

}
