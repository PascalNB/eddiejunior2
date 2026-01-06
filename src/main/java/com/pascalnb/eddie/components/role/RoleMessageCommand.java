package com.pascalnb.eddie.components.role;

import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class RoleMessageCommand extends EddieCommand<RoleComponent> {

    public RoleMessageCommand(RoleComponent component) {
        super(component, "send-message", "Create a new message with role selection buttons");
    }

    @Override
    public void accept(SlashCommandInteractionEvent event) {
        event.replyModal(getComponent().getRoleMessageModal().getEntity()).queue();
    }

}
