package com.pascalnb.eddie.components.fanart;

import com.pascalnb.eddie.ColorUtil;
import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.container.ContainerChildComponentUnion;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.ArrayList;
import java.util.List;

public class FanartRejectButton extends EddieButton<FanartComponent> {

    public FanartRejectButton(FanartComponent component) {
        super(component, "fanart-reject");
    }

    @Override
    public Button getButton() {
        return Button.danger(getId(), "Reject").withEmoji(Emoji.fromUnicode("✖️"));
    }

    @Override
    public void handle(ButtonInteractionEvent event) {
        Message message = event.getMessage();
        Container container = message.getComponentTree().getComponents().getFirst().asContainer();
        List<ContainerChildComponentUnion> currentComponents = container.getComponents();
        List<ContainerChildComponent> newComponents = new ArrayList<>(
            currentComponents.subList(0, currentComponents.size() - 1)
        );
        newComponents.add(TextDisplay.ofFormat("*Rejected by %s*", event.getUser().getAsMention()));
        Container newContainer = container.withComponents(newComponents).withAccentColor(ColorUtil.RED);
        MessageEditData editData = MessageEditBuilder.fromMessage(message)
            .setComponents(newContainer)
            .setAllowedMentions(List.of())
            .build();

        getComponent().getLogger().info(event.getUser(), "Rejected fanart submission (%s)", message.getJumpUrl());
        event.editMessage(editData).queue();
    }

}
