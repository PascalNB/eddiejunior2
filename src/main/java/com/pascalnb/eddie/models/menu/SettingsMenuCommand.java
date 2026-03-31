package com.pascalnb.eddie.models.menu;

import com.pascalnb.eddie.models.EddieCommand;
import com.pascalnb.eddie.models.EddieComponent;
import com.pascalnb.eddie.models.dynamic.DynamicSubcomponent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Collection;
import java.util.List;

public class SettingsMenuCommand<T extends EddieComponent> extends EddieCommand<T> {

    private final DynamicSubcomponent<T> dynamicSubcomponent;
    private final Collection<SettingsMenuItem> items;

    public SettingsMenuCommand(T component, String componentId, SettingsMenuItem... items) {
        this(component, componentId, List.of(items));
    }

    public SettingsMenuCommand(T component, String componentId, Collection<SettingsMenuItem> items) {
        super(component, "settings", "Change settings");
        this.items = items;
        this.dynamicSubcomponent = new DynamicSubcomponent<>(component, componentId + "-settings");
        component.register(dynamicSubcomponent);
    }

    @Override
    public void accept(SlashCommandInteractionEvent event) {
        SettingsMenu menu = dynamicSubcomponent.createInstance(register -> new SettingsMenu(
            getComponent().getConfig(),
            register,
            this.items
        ));
        event.reply(menu.getMessage()).useComponentsV2().queue();
    }

}
