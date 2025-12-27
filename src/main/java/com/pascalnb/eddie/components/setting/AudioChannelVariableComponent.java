package com.pascalnb.eddie.components.setting;

import com.pascalnb.eddie.Eddie;
import com.pascalnb.eddie.GuildManager;
import com.pascalnb.eddie.database.ComponentDatabaseManager;
import com.pascalnb.eddie.models.EddieComponentFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class AudioChannelVariableComponent extends VariableComponent<AudioChannel> {

    public AudioChannelVariableComponent(Eddie eddie, GuildManager gm, ComponentDatabaseManager db, String name) {
        super(eddie, gm, db,
            name,
            new OptionData(OptionType.CHANNEL, "channel", "channel", true)
                .setChannelTypes(ChannelType.VOICE, ChannelType.STAGE),
            o -> o.getAsChannel().asAudioChannel(),
            AudioChannel::getAsMention,
            AudioChannel::getId,
            (id) -> gm.getGuild().getChannelById(AudioChannel.class, id)
        );
    }

    public static EddieComponentFactory<AudioChannelVariableComponent> factory(String name) {
        return (eddie, gm, db) -> new AudioChannelVariableComponent(eddie, gm, db, name);
    }

}
