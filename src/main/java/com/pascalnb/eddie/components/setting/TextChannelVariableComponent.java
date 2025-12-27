package com.pascalnb.eddie.components.setting;

import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.models.EddieComponentFactory;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class TextChannelVariableComponent extends VariableComponent<TextChannel> {

    public TextChannelVariableComponent(ComponentConfig config,
        String name) {
        super(config,
            name,
            new OptionData(OptionType.CHANNEL, "channel", "channel", true)
                .setChannelTypes(ChannelType.TEXT),
            o -> o.getAsChannel().asTextChannel(),
            TextChannel::getAsMention,
            TextChannel::getId,
            config.guildManager().getGuild()::getTextChannelById
        );
    }

    public static EddieComponentFactory<TextChannelVariableComponent> factory(String name) {
        return (config) -> new TextChannelVariableComponent(config, name);
    }

}
