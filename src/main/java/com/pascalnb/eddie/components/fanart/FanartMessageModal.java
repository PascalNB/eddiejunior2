package com.pascalnb.eddie.components.fanart;

import com.pascalnb.eddie.EmbedUtil;
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
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FanartMessageModal extends EddieModal<FanartComponent> {

    public FanartMessageModal(FanartComponent component) {
        super(component, "fanart-message");
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
                    "The fanart message will be sent to the selected channel(s).",
                    EntitySelectMenu.create("channel", List.of(EntitySelectMenu.SelectTarget.CHANNEL))
                        .setChannelTypes(ChannelType.TEXT)
                        .setRequired(true)
                        .setMinValues(1)
                        .build()
                )
            )
            .build();
    }

    @Override
    public void handle(ModalInteractionEvent event) {
        String content = Objects.requireNonNull(event.getValue("message")).getAsString();
        Mentions mentions = Objects.requireNonNull(event.getValue("channel")).getAsMentions();
        List<TextChannel> channels = mentions.getChannels(TextChannel.class);

        MessageCreateData message = new MessageCreateBuilder()
            .useComponentsV2()
            .setComponents(
                Container.of(
                    TextDisplay.of(content),
                    ActionRow.of(
                        getComponent().getSubmitButton().getButton()
                    )
                )
            )
            .build();

        event.deferReply(true).queue(hook ->
            RestAction.allOf(
                channels.stream()
                    .map(channel -> channel.sendMessage(message).useComponentsV2())
                    .toList()
            ).queue(callback -> {
                String prettyChannels = channels.stream().map(TextChannel::getAsMention).collect(Collectors.joining(
                    ", "));
                getComponent().getLogger().info(event.getUser(), "Sent fanart message to %s", prettyChannels);
                hook.sendMessageEmbeds(EmbedUtil.ok("Fanart message sent to %s", prettyChannels).build()).queue();
            })
        );

    }

}
