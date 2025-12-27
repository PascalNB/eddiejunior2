package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Objects;

public class FeedbackRemoveCommand extends EddieCommand<FeedbackComponent> {

    public FeedbackRemoveCommand(FeedbackComponent component) {
        super(component, "remove-submission", "Remove a user's submission, and optionally allow them to submit again.");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
            new OptionData(OptionType.USER, "member", "member", true),
            new OptionData(OptionType.BOOLEAN, "allow-resubmit", "Allow the user to submit again.", true)
        );
    }

    @Override
    public void handle(SlashCommandInteraction event){
        Member member = Objects.requireNonNull(event.getOption("member")).getAsMember();
        boolean allowResubmit = Objects.requireNonNull(event.getOption("allow-resubmit")).getAsBoolean();
        if (member == null) {
            event.replyEmbeds(EmbedUtil.error("Invalid member").build()).setEphemeral(true).queue();
            return;
        }
        event.deferReply().queue(hook -> {
            try {
                getComponent().removeSessionSubmission(member, allowResubmit);
                if (allowResubmit) {
                    getComponent().getLogger().info(event.getUser(), "Removed submission by %s (resubmission allowed)",
                        member.getAsMention());
                    hook.sendMessageEmbeds(EmbedUtil.ok(
                        "Submission by %s removed, and they can submit again", member.getAsMention()
                    ).build()).queue();
                } else {
                    getComponent().getLogger().info(event.getUser(), "Removed submission by %s (resubmission not allowed)",
                        member.getAsMention());
                    hook.sendMessageEmbeds(EmbedUtil.ok(
                        "Submission by %s removed", member.getAsMention()
                    ).build()).queue();
                }
            } catch (CommandException e) {
                hook.sendMessageEmbeds(EmbedUtil.error(e).build()).queue();
            }
        });
    }

}
