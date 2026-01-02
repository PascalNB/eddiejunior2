package com.pascalnb.eddie.components.setting;

import com.pascalnb.eddie.database.ComponentDatabaseManager;
import com.pascalnb.eddie.exceptions.CommandException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public class Variable<T> {

    private final String name;
    private final Function<T, String> pretty;
    private final Function<T, String> serializer;
    private final ComponentDatabaseManager db;
    private @Nullable T value;

    public Variable(ComponentDatabaseManager db, String name, Function<@NotNull T, String> pretty,
        Function<@NotNull T, String> serializer, Function<String, T> deserializer, T defaultValue) {
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
        Function<@NotNull T, String> serializer, Function<String, T> deserializer) {
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

    public @Nullable T getValue() {
        return value;
    }

    public void setValue(@Nullable T value) throws CommandException {
        checkPreconditions(value);
        this.value = value;
        if (value == null) {
            this.db.removeSetting(name).stage();
        } else {
            this.db.setSetting(name, serializer.apply(value)).stage();
        }
    }

    public void apply(Consumer<@NotNull T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    @SuppressWarnings({"RedundantThrows", "unused"})
    public void checkPreconditions(@Nullable T t) throws CommandException {
    }

}
