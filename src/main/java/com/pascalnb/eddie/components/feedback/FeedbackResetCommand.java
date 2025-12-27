package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class FeedbackResetCommand extends EddieCommand<FeedbackComponent> {

    public FeedbackResetCommand(FeedbackComponent component) {
        super(component, "reset", "Reset the feedback session for all users or a specific user.");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
            new OptionData(OptionType.USER, "user", "The user whose submission should be removed.", false)
        );
    }

    @Override
    public void handle(SlashCommandInteraction event) {
        OptionMapping userOption = event.getOption("user");
        if (userOption == null) {
            try {
                getComponent().resetSession();
                event.replyEmbeds(EmbedUtil.ok("Feedback session reset").build()).queue();
            } catch (CommandException e) {
                event.replyEmbeds(EmbedUtil.error(e).build()).setEphemeral(true).queue();
            }

        } else {
            event.deferReply(false).queue(hook -> {
                try {
                    Member member = userOption.getAsMember();
                    if (member == null) {
                        hook.sendMessageEmbeds(EmbedUtil.error("Member not found").build()).queue();
                        return;
                    }
                    getComponent().resetSessionMember(userOption.getAsMember());
                    hook.sendMessageEmbeds(
                        EmbedUtil.ok(
                            "Feedback session reset for %s, they can submit again", member.getAsMention()
                        ).build()
                    ).queue();
                } catch (CommandException e) {
                    hook.sendMessageEmbeds(EmbedUtil.error(e).build()).queue();
                }
            });
        }

    }

}
