package com.pascalnb.eddie.components;

import com.pascalnb.eddie.components.setting.set.VariableSetComponent;
import com.pascalnb.eddie.models.ComponentConfig;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class BlacklistComponent extends VariableSetComponent<User> {

    public BlacklistComponent(ComponentConfig config) {
        super(config,"blacklist",
            new OptionData(OptionType.USER, "user", "user"),
            OptionMapping::getAsUser,
            User::getAsMention,
            ISnowflake::getId,
            (id) -> config.guildManager().getGuild().getJDA().retrieveUserById(id).onErrorMap(e -> null).complete());
    }

}
