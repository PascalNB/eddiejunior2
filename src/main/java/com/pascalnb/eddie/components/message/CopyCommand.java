package com.pascalnb.eddie.components.message;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieMessageCommand;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class CopyCommand extends EddieMessageCommand<MessageComponent> {

    public CopyCommand(MessageComponent component) {
        super(component, "copy message");
    }

    @Override
    public void accept(MessageContextInteractionEvent event) {
        event.deferReply(true).queue(hook -> {
            String userId = event.getUser().getId();
            MessageCreateData messageData = MessageCreateData.fromMessage(event.getTarget());
            getComponent().getClipboard().put(userId, messageData);
            hook.editOriginalEmbeds(EmbedUtil.ok("Message copied").build()).queue();
        });
    }

}
