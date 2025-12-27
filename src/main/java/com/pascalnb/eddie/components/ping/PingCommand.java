package com.pascalnb.eddie.components.ping;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class PingCommand extends EddieCommand<PingComponent> {

    public PingCommand(PingComponent component) {
        super(component, "ping", "ping");
        addPermissions(Permission.BAN_MEMBERS);
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void handle(SlashCommandInteraction event) {
        event.replyEmbeds(
            EmbedUtil.info("**Ping**: %d ms".formatted(event.getJDA().getGatewayPing())).build()
        ).queue();
    }
}