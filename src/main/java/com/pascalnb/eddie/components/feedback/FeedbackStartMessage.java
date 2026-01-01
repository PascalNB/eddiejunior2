package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieMessage;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class FeedbackStartMessage extends EddieMessage<FeedbackComponent> {

    public FeedbackStartMessage(FeedbackComponent component) {
        super(component);
    }

    @Override
    public MessageCreateData getEntity() {
        return new MessageCreateBuilder()
            .setEmbeds(EmbedUtil.ok()
                .setTitle("Feedback session started")
                .build()
            )
            .setComponents(ActionRow.of(
                getComponent().getNextButton().getEntity().withLabel("Get first song"),
                getComponent().getStopButton().getEntity()
            ))
            .build();
    }

}
