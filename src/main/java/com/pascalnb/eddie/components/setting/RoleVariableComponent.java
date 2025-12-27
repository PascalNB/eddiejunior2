package com.pascalnb.eddie.components.setting;

import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.models.EddieComponentFactory;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class RoleVariableComponent extends VariableComponent<Role> {

    public RoleVariableComponent(ComponentConfig config,
        String name) {
        super(config, name,
            new OptionData(OptionType.ROLE, "role", "role", true),
            OptionMapping::getAsRole,
            Role::getAsMention,
            Role::getId,
            config.guildManager().getGuild()::getRoleById
        );
    }

    public static EddieComponentFactory<RoleVariableComponent> factory(String name) {
        return (config) -> new RoleVariableComponent(config, name);
    }

}
