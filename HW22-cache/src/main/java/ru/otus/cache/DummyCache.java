package ru.otus.cache;

import java.util.ArrayList;
import java.util.List;

public class DummyCache<K, V> implements HwCache<K, V> {
    private final List<HwListener<K, V>> listeners = new ArrayList<>();

    @Override
    public void put(K key, V value) {
        notifyListeners(key, value, "put");
    }

    @Override
    public void remove(K key) {
        notifyListeners(key, null, "remove");
    }

    @Override
    public V get(K key) {
        notifyListeners(key, null, "get");
        return null;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        this.listeners.remove(listener);
    }

    private void notifyListeners(K key, V value, String action) {
        for (HwListener<K, V> listener : listeners) {
            listener.notify(key, value, "DUMMY " + action);
        }
    }
}
