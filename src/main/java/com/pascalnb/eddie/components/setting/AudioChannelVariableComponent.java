package com.pascalnb.eddie.components.setting;

import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.models.EddieComponentFactory;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class AudioChannelVariableComponent extends VariableComponent<AudioChannel> {

    public AudioChannelVariableComponent(ComponentConfig config,  String name) {
        super(config,
            name,
            new OptionData(OptionType.CHANNEL, "channel", "channel", true)
                .setChannelTypes(ChannelType.VOICE, ChannelType.STAGE),
            o -> o.getAsChannel().asAudioChannel(),
            AudioChannel::getAsMention,
            AudioChannel::getId,
            (id) -> config.guildManager().getGuild().getChannelById(AudioChannel.class, id)
        );
    }

    public static EddieComponentFactory<AudioChannelVariableComponent> factory(String name) {
        return (config) -> new AudioChannelVariableComponent(config, name);
    }

}
