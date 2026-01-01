package com.pascalnb.eddie.components.faq;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class FaqMessageCommand extends EddieCommand<FaqComponent> {

    public FaqMessageCommand(FaqComponent component) {
        super(component, "send-message", "Send the FAQ message. The latest message will automatically be updated");
    }

    @Override
    public void accept(SlashCommandInteractionEvent event) {
        if (getComponent().getQuestions().isEmpty()) {
            event.replyEmbeds(EmbedUtil.error("No FAQ messages added yet. User `/manage-faq edit`.").build()).setEphemeral(true).queue();
            return;
        }

        event.replyModal(getComponent().getMessageModal().getEntity()).queue();
    }

}
