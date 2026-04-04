package com.pascalnb.eddie.components.message;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.EddieMessageCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class PasteContextCommand extends EddieMessageCommand<MessageComponent> {

    public PasteContextCommand(MessageComponent component) {
        super(component, "paste message");
    }

    @Override
    public void accept(MessageContextInteractionEvent event) {
        event.deferReply(true).queue(hook -> {
            String userId = event.getUser().getId();
            MessageCreateData messageData = getComponent().getClipboard().get(userId);

            if (messageData == null) {
                hook.editOriginalEmbeds(EmbedUtil.error("Empty clipboard").build()).queue();
                return;
            }

            Message original = event.getTarget();

            if (!original.getAuthor().equals(event.getJDA().getSelfUser())) {
                hook.editOriginalEmbeds(EmbedUtil.error("Message was not sent by me").build()).queue();
                return;
            }

            original.editMessage(MessageEditData.fromCreateData(messageData))
                .queue(
                    message -> {
                        getComponent().getLogger().info(event.getUser(), "Replaced custom message: %s",
                            message.getJumpUrl());
                        hook.editOriginalEmbeds(EmbedUtil.ok("Message pasted").build()).queue();
                    },
                    e ->
                        hook.editOriginalEmbeds(EmbedUtil.error(new CommandException(e)).build()).queue()
                );
        });
    }

}
