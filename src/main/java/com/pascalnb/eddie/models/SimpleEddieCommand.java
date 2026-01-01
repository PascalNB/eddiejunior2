package com.pascalnb.eddie.models;

import net.dv8tion.jda.api.Permission;

import java.util.ArrayList;
import java.util.Collection;

public class SimpleEddieCommand<T extends EddieComponent> extends EddieCommand<T> {

    public SimpleEddieCommand(T component, String name, String description,
        Collection<? extends EddieCommand<?>> subCommands, Permission... permissions) {
        super(component, name, description, permissions);
        addSubCommands(new ArrayList<>(subCommands));
    }

}
