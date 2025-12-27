package com.pascalnb.eddie;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public final class JDAUtil {

    public static RestAction<List<@Nullable User>> retrieveUsersByIds(JDA jda, Collection<Long> ids) {
        return RestAction.allOf(
            ids.stream()
                .map(jda::retrieveUserById)
                .map(action -> action.onErrorMap(__ -> null))
                .toList()
        );
    }

}
