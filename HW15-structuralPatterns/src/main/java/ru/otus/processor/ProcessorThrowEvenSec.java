package ru.otus.processor;

import ru.otus.model.Message;

import java.time.LocalDateTime;
import java.util.Date;

public class ProcessorThrowEvenSec implements Processor {
    private final DateTimeProvider dateTimeProvider;

    public ProcessorThrowEvenSec(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public Message process(Message message) {
        LocalDateTime now = dateTimeProvider.getDate();
        if (now.getSecond() % 2 == 0) {
            throw new IllegalStateException("Even second is evil second!");
        }
        return message;
    }
}
