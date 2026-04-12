package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FeedbackListCommand extends EddieCommand<FeedbackComponent> {

    public FeedbackListCommand(FeedbackComponent component) {
        super(component, "list", "List all users in the current session");
    }

    @Override
    public void accept(SlashCommandInteractionEvent event) {
        event.deferReply().queue(hook -> {
            try {
                Map<Member, Float> members = getComponent().getQueuedMemberMultipliers();
                if (members.isEmpty()) {
                    hook.sendMessageEmbeds(EmbedUtil.error("No members in queue").build()).queue();
                    return;
                }

                String formattedMembers = members.entrySet().stream()
                    .map(entry -> "- %s (%.2f×)".formatted(entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining("\n"));

                hook.sendMessageEmbeds(EmbedUtil.ok("Members in queue:%n%s", formattedMembers).build()).queue();

            } catch (CommandException e) {
                hook.sendMessageEmbeds(EmbedUtil.error(e).build()).queue();
            }
        });

    }

}
