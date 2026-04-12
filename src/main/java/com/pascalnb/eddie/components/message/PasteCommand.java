package com.pascalnb.eddie.components.message;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.List;

public class PasteCommand extends EddieCommand<MessageComponent> {

    public PasteCommand(MessageComponent component) {
        super(component, "paste", "Paste a copied message");

        addOptions(
            new OptionData(OptionType.BOOLEAN, "ping", "Allow pings", false)
        );
    }

    @Override
    public void accept(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue(hook -> {
            String userId = event.getUser().getId();
            MessageCreateData messageData = getComponent().getClipboard().get(userId);
            boolean ping = event.getOption("ping", false, OptionMapping::getAsBoolean);

            if (messageData == null) {
                hook.editOriginalEmbeds(EmbedUtil.error("Empty clipboard").build()).queue();
                return;
            }

            if (!ping) {
                messageData = MessageCreateBuilder.from(messageData)
                    .setAllowedMentions(List.of())
                    .build();
            }

            event.getChannel().sendMessage(messageData)
                .queue(
                    message -> {
                        getComponent().getLogger().info(event.getUser(), "Sent custom message: %s",
                            message.getJumpUrl());
                        hook.editOriginalEmbeds(EmbedUtil.ok("Message pasted").build()).queue();
                    },
                    e ->
                        hook.editOriginalEmbeds(EmbedUtil.error(new CommandException(e)).build()).queue()
                );
        });
    }

}
