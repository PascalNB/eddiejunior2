package com.pascalnb.eddie.models;

import net.dv8tion.jda.api.hooks.EventListener;

import java.util.ArrayList;
import java.util.Collection;

public abstract class EddieComponent implements IEddieComponent {

    private final ComponentConfig config;
    private final Collection<EddieCommand<?>> commands = new ArrayList<>();
    private final Collection<EddieButton<?>> buttons = new ArrayList<>();
    private final Collection<EddieModal<?>> modals = new ArrayList<>();
    private final Collection<EddieStringSelector<?>> stringSelectors = new ArrayList<>();
    private final Collection<EventListener> eventListeners = new ArrayList<>();

    public EddieComponent(ComponentConfig config) {
        this.config = config;
    }

    @Override
    public final void addCommand(EddieCommand<?> command) {
        this.commands.add(command);
    }

    @Override
    public final Collection<EddieCommand<?>> getCommands() {
        return commands;
    }

    @Override
    public final void addButton(EddieButton<?> button) {
        this.buttons.add(button);
    }

    @Override
    public final Collection<EddieButton<?>> getButtons() {
        return buttons;
    }

    @Override
    public final void addModal(EddieModal<?> modal) {
        this.modals.add(modal);
    }

    @Override
    public final Collection<EddieModal<?>> getModals() {
        return modals;
    }

    @Override
    public final void addStringSelector(EddieStringSelector<?> stringSelector) {
        this.stringSelectors.add(stringSelector);
    }

    @Override
    public Collection<EddieStringSelector<?>> getStringSelectors() {
        return stringSelectors;
    }

    @Override
    public final void addEventListener(EventListener listener) {
        this.eventListeners.add(listener);
    }

    @Override
    public final Collection<EventListener> getEventListeners() {
        return eventListeners;
    }

    @Override
    public ComponentConfig getConfig() {
        return config;
    }

}
