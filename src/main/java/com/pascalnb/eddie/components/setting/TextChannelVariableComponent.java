package com.pascalnb.eddie.components.setting;

import com.pascalnb.eddie.Eddie;
import com.pascalnb.eddie.GuildManager;
import com.pascalnb.eddie.database.ComponentDatabaseManager;
import com.pascalnb.eddie.models.EddieComponentFactory;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class TextChannelVariableComponent extends VariableComponent<TextChannel> {

    public TextChannelVariableComponent(Eddie eddie, GuildManager gm, ComponentDatabaseManager db,
        String name) {
        super(eddie, gm, db,
            name,
            new OptionData(OptionType.CHANNEL, "channel", "channel", true)
                .setChannelTypes(ChannelType.TEXT),
            o -> o.getAsChannel().asTextChannel(),
            TextChannel::getAsMention,
            TextChannel::getId,
            gm.getGuild()::getTextChannelById
        );
    }

    public static EddieComponentFactory<TextChannelVariableComponent> factory(String name) {
        return (eddie, gm, db) -> new TextChannelVariableComponent(eddie, gm, db, name);
    }

}
