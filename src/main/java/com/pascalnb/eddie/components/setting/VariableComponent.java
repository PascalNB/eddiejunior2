package com.pascalnb.eddie.components.setting;

import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.models.EddieCommand;
import com.pascalnb.eddie.models.EddieComponent;
import com.pascalnb.eddie.exceptions.CommandException;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public class VariableComponent<T> extends EddieComponent {

    private final Variable<T> variable;
    private final OptionData optionData;
    private final Function<OptionMapping, T> mapper;

    public VariableComponent(ComponentConfig config,
        String name, OptionData optionData, Function<OptionMapping, T> mapper, Function<@NotNull T, String> pretty,
        Function<T, String> serializer, Function<String, T> deserializer) {
        this(config, name, optionData, mapper, pretty, serializer, deserializer, null);
    }

    public VariableComponent(ComponentConfig config,
        String name, OptionData optionData, Function<OptionMapping, T> mapper, Function<@NotNull T, String> pretty,
        Function<T, String> serializer, Function<String, T> deserializer, T defaultValue) {
        super(config);
        this.variable = new Variable<>(getDB(), name, pretty, serializer, deserializer, defaultValue);
        this.optionData = optionData.setRequired(true);
        this.mapper = mapper;

        register(
            new EddieCommand<>(this, name, "Set " + optionData.getName(), Permission.BAN_MEMBERS)
                .addSubCommands(
                    new VariableSetCommand<>(VariableComponent.this),
                    new VariableRemoveCommand<>(VariableComponent.this),
                    new VariableViewCommand<>(VariableComponent.this)
                )
        );
    }

    public String getPrettyValue() {
        return variable.getPrettyValue();
    }

    public String getName() {
        return variable.getName();
    }

    public OptionData getOptionData() {
        return optionData;
    }

    public Function<OptionMapping, T> getMapper() {
        return mapper;
    }

    public T getValue() {
        return variable.getValue();
    }

    public void setValue(T value) throws CommandException {
        checkPreconditions(value);
        variable.setValue(value);
    }

    @SuppressWarnings({"RedundantThrows", "unused"})
    public void checkPreconditions(T t) throws CommandException {
    }

    public void apply(Consumer<@NotNull T> consumer) {
        variable.apply(consumer);
    }

    public boolean hasValue() {
        return variable.hasValue();
    }

}
