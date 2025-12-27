package com.pascalnb.eddie;

import com.pascalnb.eddie.models.EddieCommand;
import com.pascalnb.eddie.models.GroupedEddieCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.requests.CompletedRestAction;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandManager {

    public record RegisterResult(List<Command> removed, List<Command> added, List<Command> edited) {
    }

    /**
     * Registers all the given commands guild commands.
     * <br>
     * Only uploads commands that are new, modifies existing commands if changed, and removes unused commands.
     *
     * @param guild      the guild instance
     * @param commands   the list of commands
     * @return the rest action
     */
    public static RestAction<RegisterResult> registerCommands(Guild guild, Collection<? extends CommandData> commands) {
        return guild.retrieveCommands()
            .flatMap(currentCommands ->
                RestAction.accumulate(
                    List.of(
                        removeUnusedCommands(guild, commands, currentCommands),
                        registerNewCommands(guild, commands, currentCommands),
                        editChangedCommands(guild, commands, currentCommands)
                    ),
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> new RegisterResult(list.getFirst(), list.get(1), list.get(2))
                    )
                )
            );
    }

    public static SlashCommandData getCommandData(EddieCommand<?> command) {
        SlashCommandData data = Commands.slash(command.getName(), command.getDescription())
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(command.getPermissions()))
            .setContexts(InteractionContextType.GUILD)
            .setIntegrationTypes(IntegrationType.GUILD_INSTALL);

        if (command instanceof GroupedEddieCommand<?> subHolder) {
            Collection<? extends EddieCommand<?>> subCommands = subHolder.getSubCommands();
            if (subCommands.isEmpty()) {
                throw new IllegalStateException(
                    "A list of subcommands cannot be empty (command: %s)".formatted(command.getName()));
            }
            for (EddieCommand<?> sub : subCommands) {
                if (sub instanceof GroupedEddieCommand<?> subSubHolder) { // is subcommand group
                    Collection<? extends EddieCommand<?>> subSubCommands = subSubHolder.getSubCommands();
                    if (subSubCommands.isEmpty()) {
                        throw new IllegalStateException(
                            "A list of subcommands cannot be empty (command: %s)".formatted(sub.getName()));
                    }
                    data = data.addSubcommandGroups(
                        new SubcommandGroupData(sub.getName(), sub.getDescription())
                            .addSubcommands(
                                // add subcommands
                                subSubCommands.stream()
                                    .map(subSub ->
                                        new SubcommandData(subSub.getName(), subSub.getDescription())
                                            .addOptions(subSub.getOptions())
                                    )
                                    .toList()
                            )
                    );
                } else { // is subcommand
                    data = data.addSubcommands(new SubcommandData(sub.getName(),
                        sub.getDescription())
                        .addOptions(sub.getOptions())
                    );
                }
            }
            return data;
        }

        return data.addOptions(command.getOptions());
    }

    private static RestAction<List<Command>> removeUnusedCommands(
        Guild guild,
        Collection<? extends CommandData> commands,
        List<Command> currentCommands
    ) {
        if (currentCommands.isEmpty()) { // no existing commands to remove
            return new CompletedRestAction<>(guild.getJDA(), List.of());
        }

        Set<String> commandNames = commands.stream()
            .map(CommandData::getName)
            .collect(Collectors.toSet());

        List<? extends RestAction<Command>> removeActions = currentCommands.stream()
            // get commands that are unused
            .filter(currentCommand -> !commandNames.contains(currentCommand.getName()))
            // delete unused commands
            .map(currentCommand ->
                guild.deleteCommandById(currentCommand.getId()).map(ignored -> currentCommand)
            )
            .toList();

        return removeActions.isEmpty()
            ? new CompletedRestAction<>(guild.getJDA(), List.of())
            : RestAction.allOf(removeActions);
    }

    private static RestAction<List<Command>> registerNewCommands(
        Guild guild,
        Collection<? extends CommandData> commands,
        List<Command> currentCommands
    ) {
        if (commands.isEmpty()) { // no new commands to upload
            return new CompletedRestAction<>(guild.getJDA(), List.of());
        }

        Set<String> currentCommandNames = currentCommands.stream()
            .map(Command::getName)
            .collect(Collectors.toSet());

        List<? extends RestAction<Command>> registerActions = commands.stream()
            // get commands that are new
            .filter(newCommand -> !currentCommandNames.contains(newCommand.getName()))
            // upload new command
            .map(guild::upsertCommand)
            .toList();

        return registerActions.isEmpty()
            ? new CompletedRestAction<>(guild.getJDA(), List.of())
            : RestAction.allOf(registerActions);
    }

    private static RestAction<List<Command>> editChangedCommands(
        Guild guild,
        Collection<? extends CommandData> commands,
        List<Command> currentCommands
    ) {
        Map<String, Command> currentCommandMap = currentCommands.stream()
            .collect(Collectors.toMap(
                Command::getName,
                Function.identity()
            ));

        List<? extends RestAction<Command>> editActions = commands.stream()
            .filter(newCommand -> {
                Command currentCommand = currentCommandMap.get(newCommand.getName());
                if (currentCommand == null) {
                    return false; // new command
                }
                CommandData currentCommandData = CommandData.fromCommand(currentCommand);
                // serialize and compare, only include different commands
                return !currentCommandData.toData().equals(newCommand.toData());
            })
            .map(newCommand -> {
                Command currentCommand = currentCommandMap.get(newCommand.getName());
                return currentCommand.editCommand().apply(newCommand);
            })
            .toList();

        return editActions.isEmpty()
            ? new CompletedRestAction<>(guild.getJDA(), List.of())
            : RestAction.allOf(editActions);

    }

}