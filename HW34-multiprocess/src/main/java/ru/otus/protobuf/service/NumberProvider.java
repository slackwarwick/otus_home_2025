package ru.otus.protobuf.service;

public class NumberProvider {
    private int rangeStart;
    private int rangeEnd;
    private int value;

    public NumberProvider() {
    }

    public int next() {
        return ++value;
    }

    public boolean hasNext() {
        return value < rangeEnd;
    }

    public void setRange(int rangeStart, int rangeEnd) {
        this.rangeStart = rangeStart;
        this.value = rangeStart;
        this.rangeEnd = rangeEnd;
    }
}
