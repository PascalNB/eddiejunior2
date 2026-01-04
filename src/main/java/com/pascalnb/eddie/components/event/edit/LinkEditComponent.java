package com.pascalnb.eddie.components.event.edit;

import com.pascalnb.eddie.components.event.EventComponent;
import com.pascalnb.eddie.components.event.LinkViewMessage;
import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.models.EddieComponentFactory;
import com.pascalnb.eddie.models.dynamic.DynamicComponent;
import com.pascalnb.eddie.models.dynamic.DynamicRegister;
import com.pascalnb.eddie.models.dynamic.DynamicSubcomponent;
import com.pascalnb.eddie.models.dynamic.UpdatingComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class LinkEditComponent extends DynamicComponent<LinkEditComponent>
    implements UpdatingComponent<LinkEditComponent> {

    private final EventComponent component;
    private final Collection<EventComponent.Link> links;
    private final EventComponent.Link selectedLink;
    private final Collection<EventComponent.Link> changes;

    private final LinkDeleteButton deleteButton;
    private final LinkEditButton editButton;
    private final LinkSelectMenu selectMenu;
    private final LinkCancelButton cancelButton;
    private final LinkAddButton addButton;
    private final LinkSubmitButton submitButton;
    private final DynamicSubcomponent<LinkEditComponent> dynamicSubcomponent;
    private final LinkEditSessionsButton editSessionsButton;

    public LinkEditComponent(ComponentConfig config, EventComponent component, DynamicRegister dynamicRegister,
        Collection<EventComponent.Link> links, @Nullable EventComponent.Link selectedLink,
        Collection<EventComponent.Link> changes
    ) {
        super(config, dynamicRegister);
        this.component = component;
        this.links = links;
        this.selectedLink = selectedLink;
        this.changes = changes;

        deleteButton = createDynamic("delete", LinkDeleteButton::new);
        editButton = createDynamic("edit", LinkEditButton::new);
        selectMenu = createDynamic("select", LinkSelectMenu::new);
        cancelButton = createDynamic("cancel", LinkCancelButton::new);
        addButton = createDynamic("add", LinkAddButton::new);
        submitButton = createDynamic("submit", LinkSubmitButton::new);
        editSessionsButton = createDynamic("session", LinkEditSessionsButton::new);
        dynamicSubcomponent = createDynamic("dyn", DynamicSubcomponent::new);
    }

    public LinkEditComponent(ComponentConfig config, EventComponent component, DynamicRegister dynamicRegister, Collection<EventComponent.Link> links) {
        this(config, component, dynamicRegister, links, null, List.of());
    }

    public static EddieComponentFactory<LinkEditComponent> factory(EventComponent component,
        DynamicRegister dynamicRegister, Collection<EventComponent.Link> links) {
        return (config) -> new LinkEditComponent(config, component, dynamicRegister, links);
    }

    public static EddieComponentFactory<LinkEditComponent> factory(EventComponent component,
        DynamicRegister dynamicRegister, Collection<EventComponent.Link> links, @Nullable EventComponent.Link selectedLink, Collection<EventComponent.Link> changes) {
        return (config) -> new LinkEditComponent(config, component, dynamicRegister, links, selectedLink, changes);
    }

    @Override
    public MessageCreateData getMessage() {
        List<ContainerChildComponent> components = new ArrayList<>();

        if (this.links.isEmpty()) {
            components.add(
                TextDisplay.of("No links yet. Use the button below to create a new link.")
            );
        } else {
            components.add(
                ActionRow.of(
                    selectMenu.getEntity()
                )
            );

            if (this.selectedLink != null) {
                LinkViewMessage viewMessage = new LinkViewMessage(this.component, this.selectedLink);
                MessageComponentTree componentTree = viewMessage.getEntity().getComponentTree();
                components.addAll(componentTree.getComponents().getFirst().asContainer().getComponents());

                List<ActionRowChildComponent> buttons = new ArrayList<>(List.of(
                    deleteButton.getEntity(),
                    editButton.getEntity()
                ));

                if (!this.selectedLink.sessions().isEmpty()) {
                    buttons.add(editSessionsButton.getEntity());
                }

                components.add(
                    ActionRow.of(buttons)
                );

            } else {
                components.add(
                    TextDisplay.of("Add a new link or select an existing link to edit/remove.")
                );
            }

            components.add(
                Separator.createDivider(Separator.Spacing.LARGE)
            );
        }

        List<ActionRowChildComponent> bottomButtons = new ArrayList<>(List.of(
            cancelButton.getEntity(),
            addButton.getEntity()
        ));

        if (!changes.isEmpty()) {
            bottomButtons.add(submitButton.getEntity());
        }

        components.add(
            ActionRow.of(bottomButtons)
        );

        Container container = Container.of(components);

        return new MessageCreateBuilder()
            .useComponentsV2()
            .setComponents(container)
            .build();
    }

    @Override
    public EddieComponentFactory<LinkEditComponent> getCloningFactory() {
        return factory(links, selectedLink, changes);
    }

    public EddieComponentFactory<LinkEditComponent> factory(Collection<EventComponent.Link> links,
        @Nullable EventComponent.Link selectedLink, Collection<EventComponent.Link> changes) {
        return factory(this.component, getDynamic(), links, selectedLink, changes);
    }

    public Collection<EventComponent.Link> getLinks() {
        return links;
    }

    public EventComponent.Link getSelectedLink() {
        return selectedLink;
    }

    public Collection<EventComponent.Link> getChanges() {
        return changes;
    }

    public DynamicSubcomponent<LinkEditComponent> getDynamicSubcomponent() {
        return dynamicSubcomponent;
    }

    public Map<String, String> getComponentsMap() {
        return this.component.getRunnableComponents().entrySet().stream()
            .map(entry -> Map.entry(entry.getKey(), entry.getValue().getRunnableTitle()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void submit() {
        this.component.updateLinks(this.links);
    }

    public EventComponent getParentComponent() {
        return this.component;
    }

    @Override
    public LinkEditComponent getComponent() {
        return this;
    }

}
