package ru.otus.services.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;
import ru.otus.lib.SensorDataBufferedWriter;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// Этот класс нужно реализовать
@SuppressWarnings({"java:S1068", "java:S125"})
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final Queue<SensorData> dataBuffer = new PriorityBlockingQueue<>(10, Comparator.comparing(SensorData::getMeasurementTime));

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
    }

    @Override
    public void process(SensorData data) {
        dataBuffer.offer(data);
        if (dataBuffer.size() >= bufferSize) {
            flush();
        }
    }

    public void flush() {
        if (dataBuffer.isEmpty()) {
            return;
        }
        try {
            List<SensorData> bufferedData = new ArrayList<>();
            synchronized (this) {
                while (!dataBuffer.isEmpty() && bufferedData.size() < bufferSize) {
                    SensorData data = dataBuffer.poll();
                    if (data != null) {
                        bufferedData.add(data);
                    }
                }
            }
            if (!bufferedData.isEmpty()) {
                synchronized (writer) {
                    writer.writeBufferedData(bufferedData);
                }
            }
        } catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}
