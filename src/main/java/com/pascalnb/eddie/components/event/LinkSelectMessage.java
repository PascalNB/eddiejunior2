package com.pascalnb.eddie.components.event;

import com.pascalnb.eddie.models.EddieMessage;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LinkSelectMessage extends EddieMessage<EventComponent> {

    private final EventComponent.Link selectedLink;

    public LinkSelectMessage(EventComponent component, @Nullable EventComponent.Link selectedLink) {
        super(component);
        this.selectedLink = selectedLink;
    }

    @Override
    public MessageCreateData getEntity() {
        List<ContainerChildComponent> components = new ArrayList<>();
        Collection<EventComponent.Link> links = getComponent().getLinks();

        if (links.isEmpty()) {
            components.add(
                TextDisplay.of("No links yet. User `/manage-event edit` to add links.")
            );
        } else {
            components.add(
                ActionRow.of(
                    getComponent().getLinkSelector().getEntity()
                )
            );

            if (this.selectedLink != null) {
                LinkViewMessage viewMessage = new LinkViewMessage(getComponent(), this.selectedLink);
                MessageComponentTree componentTree = viewMessage.getEntity().getComponentTree();
                components.addAll(componentTree.getComponents().getFirst().asContainer().getComponents());
            } else {
                components.add(
                    TextDisplay.of("Select an link to view.")
                );
            }
        }

        return new MessageCreateBuilder()
            .useComponentsV2()
            .setComponents(Container.of(components))
            .build();
    }

}
