package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieMenu;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class FeedbackStartMenu extends EddieMenu<FeedbackComponent> {

    public FeedbackStartMenu(FeedbackComponent component) {
        super(component);
    }

    @Override
    public MessageCreateData getMessage() {
        return new MessageCreateBuilder()
            .setEmbeds(EmbedUtil.ok()
                .setTitle("Feedback session started")
                .build()
            )
            .setComponents(ActionRow.of(
                getComponent().getNextButton().getButton().withLabel("Get first song"),
                getComponent().getStopButton().getButton()
            ))
            .build();
    }

}
