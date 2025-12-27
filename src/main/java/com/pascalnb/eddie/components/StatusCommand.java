package com.pascalnb.eddie.components;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.components.setting.VariableComponent;
import com.pascalnb.eddie.components.setting.set.VariableSetComponent;
import com.pascalnb.eddie.models.EddieCommand;
import com.pascalnb.eddie.models.EddieComponent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatusCommand<T extends EddieComponent & StatusComponent> extends EddieCommand<T> {

    public StatusCommand(T component) {
        super(component, "status", "Show current status");
    }

    private static void supplyStatus(StatusComponent component, Collection<Map.Entry<String, String>> status,
        List<StatusComponent> components) {

        component.supplyStatus(new StatusComponent.StatusCollector() {
            @Override
            public StatusComponent.StatusCollector addString(String name, String value) {
                status.add(Map.entry(name, value));
                return this;
            }

            @Override
            public StatusComponent.StatusCollector addVariable(String name, VariableComponent<?> variable) {
                status.add(Map.entry(name, variable.getPrettyValue()));
                return this;
            }

            @Override
            public StatusComponent.StatusCollector addSet(String name, VariableSetComponent<?> variableSet) {
                status.add(Map.entry(name, variableSet.getPrettyValues()));
                return this;
            }

            @Override
            public StatusComponent.StatusCollector addComponent(StatusComponent statusComponent) {
                components.add(statusComponent);
                return this;
            }
        });
    }

    private static Collection<Map.Entry<String, String>> getStatus(StatusComponent component) {
        List<Map.Entry<String, String>> variables = new ArrayList<>();
        List<StatusComponent> subComponents = new ArrayList<>();

        supplyStatus(component, variables, subComponents);

        for (StatusComponent subComponent : subComponents) {
            if (subComponent != null) {
                variables.addAll(getStatus(subComponent));
            }
        }

        return variables;
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void handle(SlashCommandInteraction event) {
        Collection<Map.Entry<String, String>> variables = getStatus(getComponent());
        String statusString = variables.stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> "**%s**: %s".formatted(entry.getKey(), entry.getValue()))
            .collect(Collectors.joining("\n"));

        event.replyEmbeds(
            EmbedUtil.info(statusString).build()
        ).queue();
    }

}
