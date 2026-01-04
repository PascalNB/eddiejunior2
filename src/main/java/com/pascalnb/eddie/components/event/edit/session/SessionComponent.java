package com.pascalnb.eddie.components.event.edit.session;

import com.pascalnb.eddie.components.event.EventComponent;
import com.pascalnb.eddie.components.event.LinkViewMessage;
import com.pascalnb.eddie.components.event.edit.LinkEditComponent;
import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.models.EddieComponentFactory;
import com.pascalnb.eddie.models.dynamic.DynamicComponent;
import com.pascalnb.eddie.models.dynamic.DynamicRegister;
import com.pascalnb.eddie.models.dynamic.UpdatingComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SessionComponent
    extends DynamicComponent<SessionComponent> implements UpdatingComponent<SessionComponent> {

    private final LinkEditComponent component;
    private final EventComponent.Link link;
    private final EventComponent.Session selectedSession;

    private final SessionCancelButton cancelButton;
    private final SessionSelector sessionSelector;
    private final SessionDeleteButton deleteButton;
    private final SessionEditButton editButton;
    private final SessionAddButton addButton;
    private final SessionSaveButton saveButton;

    public SessionComponent(ComponentConfig config, LinkEditComponent component,
        DynamicRegister dynamicRegister, @NotNull EventComponent.Link link,
        @Nullable EventComponent.Session selectedSession) {
        super(config, dynamicRegister);
        this.component = component;
        this.link = link;
        this.selectedSession = selectedSession;

        this.cancelButton = createDynamic("cancel", SessionCancelButton::new);
        this.sessionSelector = createDynamic("select", SessionSelector::new);
        this.deleteButton = createDynamic("delete", SessionDeleteButton::new);
        this.editButton = createDynamic("edit", SessionEditButton::new);
        this.addButton = createDynamic("add", SessionAddButton::new);
        this.saveButton = createDynamic("save", SessionSaveButton::new);
    }

    @Override
    public MessageCreateData getMessage() {
        List<ContainerChildComponent> components = new ArrayList<>();
        components.add(TextDisplay.ofFormat("## %s", link.name()));

        LinkViewMessage message = new LinkViewMessage(getParentComponent().getParentComponent(), link);
        MessageComponentTree componentTree = message.getEntity().getComponentTree();
        components.addAll(componentTree.getComponents().getFirst().asContainer().getComponents());

        components.add(
            Separator.createDivider(Separator.Spacing.SMALL)
        );

        components.add(ActionRow.of(
            sessionSelector.getEntity()
        ));

        if (selectedSession != null) {
            if (selectedSession.message() != null) {
                components.add(
                    TextDisplay.of(selectedSession.message())
                );

                components.add(ActionRow.of(
                    deleteButton.getEntity(),
                    editButton.getEntity()
                ));

            } else {
                components.add(
                    TextDisplay.of("*No message. Use the button below to add a message.*")
                );

                components.add(ActionRow.of(
                    addButton.getEntity()
                ));
            }
        }

        components.add(
            Separator.createDivider(Separator.Spacing.LARGE)
        );

        components.add(ActionRow.of(
           cancelButton.getEntity(),
           saveButton.getEntity()
        ));

        return new MessageCreateBuilder()
            .useComponentsV2()
            .setComponents(Container.of(components))
            .build();
    }

    @Override
    public EddieComponentFactory<SessionComponent> getCloningFactory() {
        return factory(link, selectedSession);
    }

    public EddieComponentFactory<SessionComponent> factory(EventComponent.Link link,
        @Nullable EventComponent.Session selectedSession) {
        return (config) -> new SessionComponent(config, this.component, getDynamic(), link, selectedSession);
    }

    public LinkEditComponent getParentComponent() {
        return this.component;
    }

    @Override
    public SessionComponent getComponent() {
        return this;
    }

    public Collection<EventComponent.Session> getSessions() {
        return this.link.sessions();
    }

    public EventComponent.Session getSelectedSession() {
        return selectedSession;
    }

    public EventComponent.Link getLink() {
        return link;
    }

}
