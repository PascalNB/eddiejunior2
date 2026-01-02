package com.pascalnb.eddie.components.setting.set;

import com.pascalnb.eddie.database.ComponentDatabaseManager;
import com.pascalnb.eddie.exceptions.CommandException;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class VariableSet<T> {

    private final ComponentDatabaseManager db;
    private final String name;
    private final Function<T, String> pretty;
    private final Set<T> values;
    private final Function<T, String> serializer;

    public VariableSet(ComponentDatabaseManager db, String name, Function<@NotNull T, String> pretty,
        Function<@NotNull T, String> serializer, Function<String, T> deserializer) {
        this.db = db;
        this.name = name;
        this.pretty = pretty;
        this.serializer = serializer;

        List<String> strings = db.getSettings(name);
        this.values = new HashSet<>(strings.stream().map(deserializer).toList());
    }

    public boolean isEmpty() {
        return values.isEmpty();
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

    public String getPrettyValue(T t) {
        return pretty.apply(t);
    }

    public Collection<T> getValues() {
        return values;
    }

    public void addValue(@NotNull T value) throws CommandException {
        checkPreconditions(value);
        if (contains(value)) {
            throw new CommandException("`%s` already contains %s".formatted(getName(), getPrettyValue(value)));
        }
        this.values.add(value);
        db.addSetting(name, serializer.apply(value)).stage();
    }

    public void checkPreconditions(@NotNull T t) throws CommandException {
    }

    public boolean contains(@NotNull T t) {
        return values.contains(t);
    }

    public String getName() {
        return name;
    }

    public boolean removeValue(@NotNull T value) {
        boolean removed = this.values.remove(value);
        if (removed) {
            db.removeSettingValue(name, serializer.apply(value)).stage();
        }
        return removed;
    }

    public void clear() {
        this.values.clear();
        db.removeSetting(name).stage();
    }

    public void replace(Set<? extends T> newValues) {
        this.values.clear();
        this.values.addAll(newValues);
        Collection<String> serialized = newValues.stream().map(serializer).toList();
        db.removeSetting(name).async(callback ->
            db.addSettings(name, serialized).await()
        );
    }

}
