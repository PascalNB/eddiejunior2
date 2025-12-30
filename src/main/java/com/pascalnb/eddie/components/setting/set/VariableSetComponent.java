package com.pascalnb.eddie.components.setting.set;

import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.models.EddieComponent;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.EddieComponentFactory;
import com.pascalnb.eddie.models.RootEddieCommand;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class VariableSetComponent<T> extends EddieComponent {

    private final VariableSet<T> set;
    private final OptionData optionData;
    private final Function<OptionMapping, T> mapper;

    public VariableSetComponent(ComponentConfig config,
        String name, OptionData optionData, Function<OptionMapping, T> mapper, Function<@NotNull T, String> pretty,
        Function<T, String> serializer, Function<String, T> deserializer) {
        super(config);

        this.set = new VariableSet<>(getDB(), name, pretty, serializer, deserializer);

        this.optionData = optionData.setRequired(true);
        this.mapper = mapper;

        addCommand(
            new RootEddieCommand<>(this, name, "Set " + this.optionData.getName(), List.of(
                new VariableSetAddCommand<>(VariableSetComponent.this),
                new VariableSetRemoveCommand<>(VariableSetComponent.this),
                new VariableSetShowCommand<>(VariableSetComponent.this),
                new VariableSetClearCommand<>(VariableSetComponent.this)
            ))
        );
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public boolean contains(T t) {
        return set.contains(t);
    }

    public String getPrettyValue(T t) {
        return set.getPrettyValue(t);
    }

    public String getPrettyValues() {
        return set.getPrettyValues();
    }

    public String getName() {
        return set.getName();
    }

    public OptionData getOptionData() {
        return optionData;
    }

    public Function<OptionMapping, T> getMapper() {
        return mapper;
    }

    public Collection<T> getValues() {
        return set.getValues();
    }

    public void addValue(T value) throws CommandException {
        checkPreconditions(value);
        this.set.addValue(value);
    }

    public boolean removeValue(T value) {
        return this.set.removeValue(value);
    }

    public void clear() {
        this.set.clear();
    }

    public void checkPreconditions(T t) throws CommandException {
    }

    public static <T> EddieComponentFactory<VariableSetComponent<T>> factory(String name, OptionData optionData,
        Function<OptionMapping, T> mapper, Function<T, String> pretty,
        Function<T, String> serializer, Function<String, T> deserializer) {
        return (config) -> new VariableSetComponent<>(config, name, optionData, mapper, pretty,
            serializer, deserializer);
    }

}
