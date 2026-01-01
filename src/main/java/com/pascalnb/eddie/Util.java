package com.pascalnb.eddie;

import java.util.*;
import java.util.stream.Collectors;

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

    @SuppressWarnings("unchecked")
    public static <T> T normalizeJson(T value) {
        return (T) normalizeObject(value);
    }

    private static Object normalizeObject(Object value) {
        if (value instanceof Map<?, ?> map) {
            return map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Object::toString)))
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> normalizeJson(e.getValue()),
                    (a, b) -> a,
                    LinkedHashMap::new
                ));
        }

        if (value instanceof Collection<?> list) {
            return list.stream()
                .map(Util::normalizeJson)
                .sorted(Comparator.comparing(Object::toString))
                .collect(Collectors.toList());
        }

        return value;
    }


}
