package com.pascalnb.eddie.components.setting.set;

import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.models.EddieComponent;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.EddieComponentFactory;
import com.pascalnb.eddie.models.RootEddieCommand;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.*;
import java.util.function.Function;

public class VariableSetComponent<T> extends EddieComponent {

    private final String name;
    private final OptionData optionData;
    private final Function<OptionMapping, T> mapper;
    private final Function<T, String> pretty;
    private final Set<T> values;
    private final Function<T, String> serializer;
    private final Function<String, T> deserializer;

    public VariableSetComponent(ComponentConfig config,
        String name, OptionData optionData, Function<OptionMapping, T> mapper, Function<T, String> pretty,
        Function<T, String> serializer, Function<String, T> deserializer) {
        super(config);
        this.name = name;
        this.optionData = optionData.setRequired(true);
        this.mapper = mapper;
        this.pretty = pretty;
        this.serializer = serializer;
        this.deserializer = deserializer;

        List<String> strings = getDB().getSettings(name);
        this.values = new HashSet<>(strings.stream().map(deserializer).toList());

        addCommand(
            new RootEddieCommand<>(this, this.name, "Set " + this.optionData.getName(), List.of(
                new VariableSetAddCommand<>(VariableSetComponent.this),
                new VariableSetRemoveCommand<>(VariableSetComponent.this),
                new VariableSetShowCommand<>(VariableSetComponent.this),
                new VariableSetClearCommand<>(VariableSetComponent.this)
            ))
        );
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public boolean contains(T t) {
        return values.contains(t);
    }

    public String getPrettyValue(T t) {
        return pretty.apply(t);
    }

    public String getPrettyValues() {
        if (values.isEmpty()) {
            return "Not set";
        }
        return values.stream()
            .map(this::getPrettyValue)
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
    }

    public String getName() {
        return name;
    }

    public OptionData getOptionData() {
        return optionData;
    }

    public Function<OptionMapping, T> getMapper() {
        return mapper;
    }

    public Collection<T> getValues() {
        return values;
    }

    public void addValue(T value) throws CommandException {
        checkPreconditions(value);
        this.values.add(value);
        getDB().addSetting(name, serializer.apply(value)).stage();
    }

    public boolean removeValue(T value) {
        boolean removed = this.values.remove(value);
        if (removed) {
            getDB().removeSettingValue(name, serializer.apply(value)).stage();
        }
        return removed;
    }

    public void clear() {
        this.values.clear();
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
