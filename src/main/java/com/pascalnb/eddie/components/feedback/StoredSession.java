package com.pascalnb.eddie.components.feedback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Set;
import java.util.stream.Collectors;

public record StoredSession(long epoch, Set<String> winnerIds, Set<String> submissionIds) {

    public static final StoredSession DEFAULT = new StoredSession(0, Set.of(), Set.of());

    public static StoredSession fromString(String json) {
        JSONObject object = new JSONObject(json);
        return new StoredSession(
            object.getLong("epoch"),
            object.getJSONArray("winnerIds").toList()
                .stream()
                .map(String.class::cast)
                .collect(Collectors.toSet()),
            object.getJSONArray("submissionIds").toList()
                .stream()
                .map(String.class::cast)
                .collect(Collectors.toSet())
        );
    }

    public @NotNull String toString() {
        return new JSONObject()
            .put("epoch", epoch())
            .put("winnerIds", winnerIds())
            .put("submissionIds", submissionIds())
            .toString();
    }

}
