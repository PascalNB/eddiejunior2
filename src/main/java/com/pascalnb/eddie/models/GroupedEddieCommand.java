package com.pascalnb.eddie.models;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class GroupedEddieCommand<T extends EddieComponent> extends EddieCommand<T> {

    private final Collection<EddieCommand<?>> subCommands = new ArrayList<>();

    public GroupedEddieCommand(T component, String name, String description) {
        super(component, name, description);
    }

    public GroupedEddieCommand(T component, String name, String description, Permission... permissions) {
        super(component, name, description);
        addPermissions(permissions);
    }

    public void addSubCommand(EddieCommand<?> subCommand) {
        this.subCommands.add(subCommand);
    }

    public void addSubCommands(Collection<EddieCommand<?>> subCommands) {
        this.subCommands.addAll(subCommands);
    }

    public Collection<EddieCommand<?>> getSubCommands() {
        return subCommands;
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void handle(SlashCommandInteraction event) {
        String[] command = event.getFullCommandName().split(" ");

        for (int i = 0; i < command.length; i++) {
            if (command[i].equals(getName())) {
                int j = i + 1;
                for (EddieCommand<?> subCommand : subCommands) {
                    if (subCommand.getName().equals(command[j])) {
                        subCommand.handle(event);
                        return;
                    }
                }
                return;
            }
        }
    }

}