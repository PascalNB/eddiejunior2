package com.pascalnb.eddie.components.modmail;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class ModmailArchiveCommand extends EddieCommand<ModmailComponent> {

    public ModmailArchiveCommand(ModmailComponent component) {
        super(component, "archive", "Archive the current modmail thread.");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void handle(SlashCommandInteraction event) {
        if (!event.getChannel().getType().equals(ChannelType.GUILD_PRIVATE_THREAD)
            || !event.getChannel().getType().equals(ChannelType.GUILD_PUBLIC_THREAD)) {
            event.replyEmbeds(EmbedUtil.error("This command can only be used in a modmail thread.").build()).queue();
            return;
        }

        event.replyEmbeds(EmbedUtil.ok("Archiving thread").build())
            .queue(callback -> {
                    getComponent().getLogger().info(event.getUser(), "Archived thread %s",
                        event.getChannel().getAsMention());
                    event.getChannel().asThreadChannel().getManager().setLocked(true).setArchived(true).queue();
                }
            );
    }

}
