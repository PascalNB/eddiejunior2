package com.pascalnb.eddie.components.faq;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.EddieModal;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.List;
import java.util.Objects;

public class FaqMessageModal extends EddieModal<FaqComponent> {

    public FaqMessageModal(FaqComponent component) {
        super(component, "faq-message");
    }

    @Override
    public Modal getModal() {
        return Modal.create(getId(), "Create message")
            .addComponents(
                Label.of(
                    "Message",
                    "Markdown formatting is supported.",
                    TextInput.create("message", TextInputStyle.PARAGRAPH)
                        .setRequired(true)
                        .build()
                ),
                Label.of(
                    "Channel",
                    "The FAQ message will be sent to the selected channel(s).",
                    EntitySelectMenu.create("channel", List.of(EntitySelectMenu.SelectTarget.CHANNEL))
                        .setChannelTypes(ChannelType.TEXT)
                        .setRequired(true)
                        .setMinValues(1)
                        .setMaxValues(1)
                        .build()
                )
            )
            .build();
    }

    @Override
    public void handle(ModalInteractionEvent event) {
        String content = Objects.requireNonNull(event.getValue("message")).getAsString();
        Mentions mentions = Objects.requireNonNull(event.getValue("channel")).getAsMentions();
        TextChannel channel = mentions.getChannels(TextChannel.class).getFirst();

        MessageCreateData messageData = new MessageCreateBuilder()
            .useComponentsV2()
            .setComponents(
                Container.of(
                    TextDisplay.of(content),
                    ActionRow.of(
                        getComponent().getSelector().getMenu()
                    )
                )
            )
            .build();

        event.deferReply(true).queue(hook ->
            channel.sendMessage(messageData).useComponentsV2().queue(message -> {
                try {
                    getComponent().setMessage(message);
                } catch (CommandException e) {
                    hook.sendMessageEmbeds(EmbedUtil.error(e).build()).queue();
                    return;
                }
                getComponent().getLogger().info(event.getUser(), "Sent FAQ message to %s", channel.getAsMention());
                hook.sendMessageEmbeds(EmbedUtil.ok("FAQ message sent to %s", channel.getAsMention()).build()).queue();
            })
        );

    }

}
