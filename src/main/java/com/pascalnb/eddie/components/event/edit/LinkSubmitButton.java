package com.pascalnb.eddie.components.event.edit;

import com.pascalnb.eddie.ColorUtil;
import com.pascalnb.eddie.components.event.EventComponent;
import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class LinkSubmitButton extends EddieButton<LinkEditComponent> {

    public LinkSubmitButton(LinkEditComponent component, String id) {
        super(component, id);
    }

    @Override
    public Button getEntity() {
        return Button.success(getId(), "Submit").withEmoji(Emoji.fromUnicode("✔️"));
    }

    @Override
    public void accept(ButtonInteractionEvent event) {
        event.deferEdit().queue(hook -> {
            getComponent().submit();
            Collection<EventComponent.Link> changes = getComponent().getChanges();
            List<String> changesLinks = changes.stream().map(EventComponent.Link::name).toList();
            String formattedChanges = changesLinks.stream()
                .map("- **%s**"::formatted)
                .collect(Collectors.joining("\n"));

            List<ContainerChildComponent> components = new ArrayList<>(List.of(
                TextDisplay.of("## Changes saved"),
                Separator.createDivider(Separator.Spacing.SMALL),
                TextDisplay.ofFormat("""
                        Changed event links:
                        %s
                        """, formattedChanges)
            ));

            hook.editOriginal(new MessageEditBuilder()
                .useComponentsV2()
                .setComponents(
                    Container.of(components).withAccentColor(ColorUtil.GREEN)
                )
                .build()).useComponentsV2().queue(callback -> getComponent().unmount());
        });
    }

}
