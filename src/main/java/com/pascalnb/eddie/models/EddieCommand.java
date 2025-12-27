package com.pascalnb.eddie.models;

import com.pascalnb.eddie.Eddie;
import com.pascalnb.eddie.Handler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class EddieCommand<T extends EddieComponent> implements Handler<SlashCommandInteraction> {

    private final T component;
    private final String name;
    private final String description;
    private final List<Permission> permissions = new ArrayList<>();

    public EddieCommand(T component, String name, String description) {
        this.component = component;
        this.name = name;
        this.description = description;
        addPermissions(Permission.USE_APPLICATION_COMMANDS);
    }

    public abstract List<OptionData> getOptions();

    public T getComponent() {
        return component;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void addPermissions(Permission... permissions) {
        Collections.addAll(this.permissions, permissions);
    }

    public List<Permission> getPermissions() {
        return permissions;
    }
}