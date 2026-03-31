package com.pascalnb.eddie;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class Util {

    public static <T> Collector<T, ?, List<T>> topK(int k, Comparator<? super T> comparator) {
        BiConsumer<PriorityQueue<T>, T> accumulator = (heap, item) -> {
            if (heap.size() < k) {
                heap.offer(item);
            } else if (comparator.compare(item, heap.peek()) > 0) {
                heap.poll();
                heap.offer(item);
            }
        };

        return Collector.of(
            () -> new PriorityQueue<>(k, comparator),
            accumulator,

            (left, right) -> {
                for (T item : right) {
                    accumulator.accept(left, item);
                }
                return left;
            },

            heap -> {
                List<T> result = new ArrayList<>(heap);
                result.sort(comparator.reversed());
                return Collections.unmodifiableList(result);
            }
        );
    }

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
            try {
                return map.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.comparing(Object::toString)))
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> normalizeJson(e.getValue()),
                        (a, b) -> a,
                        LinkedHashMap::new
                    ));
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null;
            }
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
