package com.pascalnb.eddie.models.menu;

import com.pascalnb.eddie.models.*;
import com.pascalnb.eddie.models.dynamic.DynamicComponent;
import com.pascalnb.eddie.models.dynamic.DynamicRegister;
import com.pascalnb.eddie.models.dynamic.UpdatingComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SettingsMenu extends DynamicComponent<SettingsMenu> implements UpdatingComponent<SettingsMenu> {

    private final List<SettingsMenuItem> items = new ArrayList<>();
    private final Map<String, EntityProvider<ActionRowChildComponent>> components = new HashMap<>();
    private final SettingsMenuCloseButton closeButton;

    public SettingsMenu(ComponentConfig config, DynamicRegister dynamicRegister, Collection<SettingsMenuItem> items) {
        super(config, dynamicRegister);
        this.items.addAll(items);

        IntStream.range(0, this.items.size()).forEach(i -> {
            SettingsMenuItem item = this.items.get(i);
            components.put(item.getTitle(), createDynamic(String.valueOf(i), item::getSubcomponent));
        });

        closeButton = createDynamic("close", SettingsMenuCloseButton::new);
    }

    @Override
    public MessageCreateData getMessage() {
        List<ContainerChildComponent> childComponents = this.components.entrySet().stream()
            .flatMap(entry ->
                Stream.of(
                    TextDisplay.of(entry.getKey()),
                    ActionRow.of(entry.getValue().getEntity()),
                    Separator.createDivider(Separator.Spacing.SMALL)
                )
            ).collect(Collectors.toCollection(ArrayList::new));
        childComponents.add(
            ActionRow.of(closeButton.getEntity())
        );

        return new MessageCreateBuilder()
            .useComponentsV2()
            .setComponents(Container.of(childComponents))
            .build();
    }

    @Override
    public EddieComponentFactory<SettingsMenu> getCloningFactory() {
        return config -> new SettingsMenu(config, getDynamic(), this.items);
    }

    @Override
    public SettingsMenu getComponent() {
        return this;
    }

}
