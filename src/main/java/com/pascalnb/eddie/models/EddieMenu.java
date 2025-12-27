package com.pascalnb.eddie.models;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.*;

public abstract class EddieMenu<T extends EddieComponent> {

    private final T component;
    private final List<Permission> permissions = new ArrayList<>();
    private final List<EddieButton<?>> buttons = new ArrayList<>();

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

    public T getComponent() {
        return component;
    }

}
