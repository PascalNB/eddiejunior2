package com.pascalnb.eddie.components.setting;

import com.pascalnb.eddie.Eddie;
import com.pascalnb.eddie.GuildManager;
import com.pascalnb.eddie.database.ComponentDatabaseManager;
import com.pascalnb.eddie.models.EddieComponentFactory;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class RoleVariableComponent extends VariableComponent<Role> {

    public RoleVariableComponent(Eddie eddie, GuildManager gm, ComponentDatabaseManager db,
        String name) {
        super(eddie, gm, db, name,
            new OptionData(OptionType.ROLE, "role", "role", true),
            OptionMapping::getAsRole,
            Role::getAsMention,
            Role::getId,
            gm.getGuild()::getRoleById
        );
    }

    public static EddieComponentFactory<RoleVariableComponent> factory(String name) {
        return (eddie, gm, db) -> new RoleVariableComponent(eddie, gm, db, name);
    }

}
