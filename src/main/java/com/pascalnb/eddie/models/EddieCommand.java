package com.pascalnb.eddie.models;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.*;

public class EddieCommand<T extends EddieComponent> extends EddieSubcomponentBase<SlashCommandData,
    SlashCommandInteractionEvent, T> {

    private final String description;
    private final Set<Permission> permissions = new HashSet<>();
    private final Collection<OptionData> options = new ArrayList<>();
    private final Collection<EddieCommand<?>> subCommands = new ArrayList<>();

    public EddieCommand(T component, String name, String description, Permission... permissions) {
        this(component, name, description);
        addPermissions(permissions);
    }

    public EddieCommand(T component, String name, String description) {
        super(component, name);
        this.description = description;
        addPermissions(Permission.USE_APPLICATION_COMMANDS);
    }

    public EddieCommand<T> addPermissions(Permission... permissions) {
        Collections.addAll(this.permissions, permissions);
        return this;
    }

    public EddieCommand<T> addSubCommands(Collection<EddieCommand<?>> subCommands) {
        this.subCommands.addAll(subCommands);
        return this;
    }

    public EddieCommand<T> addSubCommands(EddieCommand<?>... subCommands) {
        Collections.addAll(this.subCommands, subCommands);
        return this;
    }

    @Override
    public SlashCommandData getEntity() {
        SlashCommandData data = Commands.slash(getId(), getDescription())
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(getPermissions()))
            .setContexts(InteractionContextType.GUILD)
            .setIntegrationTypes(IntegrationType.GUILD_INSTALL);

        if (hasSubCommands()) {
            for (EddieCommand<?> subCommand : this.subCommands) {
                if (subCommand.hasSubCommands()) {
                    data = data.addSubcommandGroups(
                        new SubcommandGroupData(subCommand.getId(), subCommand.getDescription())
                            .addSubcommands(
                                // add subcommands
                                subCommand.getSubCommands().stream()
                                    .map(subSub ->
                                        new SubcommandData(subSub.getId(), subSub.getDescription())
                                            .addOptions(subSub.getOptions())
                                    )
                                    .toList()
                            )
                    );
                } else {
                    data = data.addSubcommands(
                        new SubcommandData(subCommand.getId(), subCommand.getDescription())
                            .addOptions(subCommand.getOptions())
                    );
                }
            }
        } else {
            data.addOptions(getOptions());
        }
        return data;
    }

    public String getDescription() {
        return description;
    }

    public Collection<Permission> getPermissions() {
        return permissions;
    }

    public boolean hasSubCommands() {
        return !getSubCommands().isEmpty();
    }

    public Collection<EddieCommand<?>> getSubCommands() {
        return subCommands;
    }

    public Collection<OptionData> getOptions() {
        return options;
    }

    @Override
    public Class<SlashCommandData> getEntityType() {
        return SlashCommandData.class;
    }

    public EddieCommand<T> addOptions(OptionData... options) {
        Collections.addAll(this.options, options);
        return this;
    }

    public EddieCommand<T> addOptions(Collection<OptionData> options) {
        this.options.addAll(options);
        return this;
    }

    @Override
    public void accept(SlashCommandInteractionEvent event) {
        String[] command = event.getFullCommandName().split(" ");

        for (int i = 0; i < command.length; i++) {
            if (command[i].equals(getId())) {
                int j = i + 1;
                for (EddieCommand<?> subCommand : subCommands) {
                    if (subCommand.getId().equals(command[j])) {
                        subCommand.accept(event);
                        return;
                    }
                }
                return;
            }
        }
    }

    @Override
    public Class<SlashCommandInteractionEvent> getType() {
        return SlashCommandInteractionEvent.class;
    }

}