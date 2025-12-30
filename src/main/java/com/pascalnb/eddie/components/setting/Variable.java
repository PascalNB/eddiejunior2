package com.pascalnb.eddie.components.setting;

import com.pascalnb.eddie.database.ComponentDatabaseManager;
import com.pascalnb.eddie.exceptions.CommandException;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public class Variable<T> {

    private final String name;
    private final Function<T, String> pretty;
    private final Function<T, String> serializer;
    private T value;
    private final ComponentDatabaseManager db;

    public Variable(ComponentDatabaseManager db, String name, Function<@NotNull T, String> pretty,
        Function<T, String> serializer, Function<String, T> deserializer, T defaultValue) {
        this.db = db;
        this.name = name;
        this.pretty = pretty;
        this.serializer = serializer;

        String valueString = db.getSetting(name);
        this.value = valueString != null
            ? deserializer.apply(valueString)
            : defaultValue;
    }

    public Variable(ComponentDatabaseManager db, String name, Function<@NotNull T, String> pretty,
        Function<T, String> serializer, Function<String, T> deserializer) {
        this(db, name, pretty, serializer, deserializer, null);
    }

    public boolean hasValue() {
        return value != null;
    }

    public String getPrettyValue() {
        if (value == null) {
            return "Not set";
        }
        return pretty.apply(value);
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) throws CommandException {
        checkPreconditions(value);
        this.value = value;
        if (value == null) {
            this.db.removeSetting(name).stage();
        } else {
            this.db.setSetting(name, serializer.apply(value)).stage();
        }
    }

    public void apply(Consumer<T> consumer) {
        if (hasValue()) {
            consumer.accept(value);
        }
    }

    @SuppressWarnings({"RedundantThrows", "unused"})
    public void checkPreconditions(T t) throws CommandException {
    }

}
