package ru.otus.cache;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class HwKey<T> {
    private final T key;

    private HwKey(T key) {
        this.key = key;
    }

    public static <T> HwKey<T> of(T key) {
        return new HwKey<>(key);
    }
}
