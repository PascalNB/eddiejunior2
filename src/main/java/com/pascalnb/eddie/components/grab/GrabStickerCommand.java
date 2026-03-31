package com.pascalnb.eddie.components.grab;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieMessageCommand;
import net.dv8tion.jda.api.entities.sticker.StickerItem;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

import java.util.List;

public class GrabStickerCommand extends EddieMessageCommand<GrabComponent> {

    public GrabStickerCommand(GrabComponent component) {
        super(component, "grab sticker");
    }

    @Override
    public void accept(MessageContextInteractionEvent event) {
        List<StickerItem> stickers = event.getTarget().getStickers();
        if (stickers.isEmpty()) {
            event.replyEmbeds(EmbedUtil.error("Message does not contain any stickers").build())
                .setEphemeral(true).queue();
            return;
        }

        String url = stickers.getFirst().getIcon().getUrl(1024);
        event.reply(url).setEphemeral(true).queue();
    }

}
