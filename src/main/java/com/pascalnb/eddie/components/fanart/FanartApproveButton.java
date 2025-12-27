package com.pascalnb.eddie.components.fanart;

import com.pascalnb.eddie.ColorUtil;
import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.container.ContainerChildComponentUnion;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.ArrayList;
import java.util.List;

public class FanartApproveButton extends EddieButton<FanartComponent> {

    public FanartApproveButton(FanartComponent component) {
        super(component, "fanart-approve", "Approve");
    }

    @Override
    public Button getButton() {
        return Button.success(getId(), getLabel()).withEmoji(Emoji.fromUnicode("✔️"));
    }

    @Override
    public void handle(ButtonInteractionEvent event) {
        Message message = event.getMessage();
        Container container = message.getComponentTree().getComponents().getFirst().asContainer();
        List<ContainerChildComponentUnion> currentComponents = container.getComponents();

        // Edited submission
        List<ContainerChildComponent> newComponents = new ArrayList<>(
            currentComponents.subList(0, currentComponents.size() - 1)
        );
        newComponents.add(TextDisplay.ofFormat("*Approved by %s*", event.getUser().getAsMention()));
        Container newContainer = container.withComponents(newComponents).withAccentColor(ColorUtil.GREEN);
        MessageEditData editData = MessageEditBuilder.fromMessage(message)
            .setComponents(newContainer)
            .setAllowedMentions(List.of())
            .build();

        // Forwarded submission
        List<ContainerChildComponent> postComponents = new ArrayList<>(
            currentComponents.subList(0, currentComponents.size() - 2)
        );
        Container postContainer = Container.of(postComponents);
        MessageCreateData postData = MessageCreateBuilder.fromMessage(message)
            .setComponents(postContainer)
            .setAllowedMentions(List.of())
            .build();

        try {
            RestAction<Message> forwardAction = getComponent().forwardSubmission(postData);
            event.deferEdit().queue(hook ->
                forwardAction.queue(forwardMessage -> {
                    try {
                        forwardMessage.addReaction(Emoji.fromUnicode("❤️")).queue();
                    } catch (InsufficientPermissionException e) {
                        getComponent().getLogger().error(e);
                    }
                    getComponent().getLogger().info(event.getUser(), "Approved fanart submission (%s)",
                        forwardMessage.getJumpUrl());
                    hook.editOriginal(editData).queue();
                })
            );
        } catch (CommandException e) {
            event.replyEmbeds(EmbedUtil.error(e).build()).setEphemeral(true).queue();
        }
    }

}
