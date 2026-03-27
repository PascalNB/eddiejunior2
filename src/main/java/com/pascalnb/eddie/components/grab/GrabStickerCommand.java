package com.pascalnb.eddie.components.grab;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieSubcomponentBase;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.sticker.StickerItem;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.List;

public class GrabStickerCommand extends EddieSubcomponentBase<CommandData, MessageContextInteractionEvent, GrabComponent> {

    public GrabStickerCommand(GrabComponent component) {
        super(component, "grab sticker");
    }

    @Override
    public CommandData getEntity() {
        return Commands.message(getId())
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS))
            .setContexts(InteractionContextType.GUILD)
            .setIntegrationTypes(IntegrationType.GUILD_INSTALL);
    }

    @Override
    public Class<CommandData> getEntityType() {
        return CommandData.class;
    }

    @Override
    public Class<MessageContextInteractionEvent> getEventType() {
        return MessageContextInteractionEvent.class;
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
