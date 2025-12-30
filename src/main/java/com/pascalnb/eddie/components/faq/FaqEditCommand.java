package com.pascalnb.eddie.components.faq;

import com.pascalnb.eddie.components.faq.edit.FaqEditComponent;
import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class FaqEditCommand extends EddieCommand<FaqComponent> {

    public FaqEditCommand(FaqComponent component) {
        super(component, "edit", "Edit or remove FAQs");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void handle(SlashCommandInteraction event) {
        event.deferReply().queue(hook -> {
            FaqEditComponent editMenu = getComponent().createEditMenu();
            hook.sendMessage(editMenu.getMessage()).queue();
        });

    }

}
