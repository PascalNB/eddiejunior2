package com.pascalnb.eddie.models;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class EddieMessageCommand<T extends EddieComponent> extends EddieSubcomponentBase<CommandData,
    MessageContextInteractionEvent, T> {

    private final Set<Permission> permissions = new HashSet<>();

    public EddieMessageCommand(T component, String name) {
        super(component, name);
        addPermissions(Permission.USE_APPLICATION_COMMANDS);
    }

    public EddieMessageCommand(T component, String name, Permission... permissions) {
        this(component, name);
        addPermissions(permissions);
    }

    public EddieMessageCommand<T> addPermissions(Permission... permissions) {
        Collections.addAll(this.permissions, permissions);
        return this;
    }

    public EddieMessageCommand<T> addPermissions(Collection<Permission> permissions) {
        this.permissions.addAll(permissions);
        return this;
    }

    @Override
    public CommandData getEntity() {
        return Commands.message(getId())
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(getPermissions()))
            .setContexts(InteractionContextType.GUILD)
            .setIntegrationTypes(IntegrationType.GUILD_INSTALL);
    }

    @Override
    public Class<CommandData> getEntityType() {
        return CommandData.class;
    }

    @Override
    public Class<MessageContextInteractionEvent> getEventType() {
        return MessageContextInteractionEvent.class;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

}
