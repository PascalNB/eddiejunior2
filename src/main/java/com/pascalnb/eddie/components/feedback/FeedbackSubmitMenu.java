package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.models.EddieMenu;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class FeedbackSubmitMenu extends EddieMenu<FeedbackComponent> {

    public FeedbackSubmitMenu(FeedbackComponent component) {
        super(component);
    }

    @Override
    public MessageCreateData getMessage() {
        return new MessageCreateBuilder()
            .setComponents(
                ActionRow.of(
                    getComponent().getSubmitButton().getButton()
                )
            )
            .build();
    }

}
