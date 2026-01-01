package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.models.EddieMessage;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class FeedbackSubmitMessage extends EddieMessage<FeedbackComponent> {

    public FeedbackSubmitMessage(FeedbackComponent component) {
        super(component);
    }

    @Override
    public MessageCreateData getEntity() {
        return new MessageCreateBuilder()
            .setComponents(
                ActionRow.of(
                    getComponent().getSubmitButton().getEntity()
                )
            )
            .build();
    }

}
