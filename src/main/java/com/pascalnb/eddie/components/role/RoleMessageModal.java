package com.pascalnb.eddie.components.role;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieModal;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.requests.CompletedRestAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RoleMessageModal extends EddieModal<RoleComponent> {

    public RoleMessageModal(RoleComponent component) {
        super(component, "role-message");
    }

    @Override
    public Modal getEntity() {
        return Modal.create(getId(), "Create role message")
            .addComponents(
                Label.of(
                    "Role",
                    EntitySelectMenu.create("role", EntitySelectMenu.SelectTarget.ROLE)
                        .setRequired(true)
                        .setMinValues(1)
                        .setMaxValues(1)
                        .build()
                ),
                Label.of(
                    "Message",
                    "Optional message that is sent with the role buttons.",
                    TextInput.create("message", TextInputStyle.PARAGRAPH)
                        .setRequired(false)
                        .build()
                ),
                Label.of(
                    "Channel",
                    "The channels where the message with buttons will be sent to.",
                    EntitySelectMenu.create("channel", EntitySelectMenu.SelectTarget.CHANNEL)
                        .setChannelTypes(ChannelType.TEXT)
                        .setRequired(true)
                        .setMinValues(1)
                        .build()
                )
            )
            .build();
    }

    @Override
    public void accept(ModalInteractionEvent event) {
        event.deferReply().queue(hook -> {
            Role role = Objects.requireNonNull(event.getValue("role")).getAsMentions().getRoles().getFirst();
            String message = Objects.requireNonNull(event.getValue("message")).getAsString();
            if (message.isBlank()) {
                message = null;
            }
            List<TextChannel> channels = Objects.requireNonNull(event.getValue("channel")).getAsMentions()
                .getChannels(TextChannel.class);

            List<ContainerChildComponent> components = new ArrayList<>();

            if (message != null) {
                components.add(
                    TextDisplay.of(message)
                );
            }

            String addId = RoleComponent.BUTTON_ID_PREFIX + "a_" + role.getId();
            String removeId = RoleComponent.BUTTON_ID_PREFIX + "r_" + role.getId();

            components.add(
                ActionRow.of(
                    Button.success(addId, "Get %s role".formatted(role.getName())).withEmoji(Emoji.fromUnicode("➕")),
                    Button.danger(removeId, "Remove %s role".formatted(role.getName())).withEmoji(
                        Emoji.fromUnicode("\uD83D\uDDD1️"))
                )
            );

            MessageCreateData createData = new MessageCreateBuilder()
                .useComponentsV2()
                .setComponents(Container.of(components))
                .build();

            RestAction.allOf(
                channels.stream()
                .map(channel -> {
                    try {
                        return channel.sendMessage(createData).useComponentsV2(createData.isUsingComponentsV2());
                    } catch (InsufficientPermissionException e) {
                        getComponent().getLogger().error("Unable to send role message in %s", channel.getAsMention());
                        return new CompletedRestAction<>(channel.getJDA(), (Message) null);
                    }
                })
                .toList()
            ).queue(callback -> {
                String prettyChannels = callback.stream()
                    .filter(Objects::nonNull)
                    .map(m -> m.getChannel().getAsMention())
                    .collect(Collectors.joining(", "));
                getComponent().getLogger().info(event.getUser(), "Sent role (%s) message to %s", role.getAsMention(),
                    prettyChannels);
                hook.sendMessageEmbeds(EmbedUtil.ok("Role message sent to %s", prettyChannels).build()).queue();
            });
        });
    }

}
