package com.pascalnb.eddie.models;

import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.*;

public abstract class EddieMenu<T extends IEddieComponent> {

    private final T component;
    private final List<EddieButton<?>> buttons = new ArrayList<>();
    private final List<EddieStringSelector<?>> stringSelectors = new ArrayList<>();

    public EddieMenu(T component) {
        this.component = component;
    }

    public abstract MessageCreateData getMessage();

    public void addButton(EddieButton<?> button) {
        this.buttons.add(button);
    }

    public List<EddieButton<?>> getButtons() {
        return buttons;
    }

    public void addStringSelector(EddieStringSelector<?> stringSelector) {
        this.stringSelectors.add(stringSelector);
    }

    public List<EddieStringSelector<?>> getStringSelectors() {
        return stringSelectors;
    }

    public T getComponent() {
        return component;
    }

}
