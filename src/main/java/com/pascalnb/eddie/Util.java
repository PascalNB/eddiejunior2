package com.pascalnb.eddie;

import com.pascalnb.eddie.exceptions.CommandException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Util {

    public static <T> List<T> spread(Object... items) {
        List<T> result = new ArrayList<>();
        for (Object item : items) {
            if (item instanceof Collection<?>) {
                for (Object element : (Collection<?>) item) {
                    // noinspection unchecked
                    result.add((T) element);
                }
            } else {
                // noinspection unchecked
                result.add((T) item);
            }
        }
        return result;
    }

    public interface ThrowingConsumer<T> {
        void accept(T t) throws CommandException;
    }


}
