package ru.otus.listener.homework;

import java.util.*;
import ru.otus.listener.Listener;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;

public class HistoryListener implements Listener, HistoryReader {

    private final Map<Long, Message> messages = new HashMap<>();

    @Override
    public void onUpdated(Message msg) {
        ObjectForMessage field13 = clonedField13(msg.getField13());
        messages.put(msg.getId(), msg.toBuilder().field13(field13).build());
    }

    private ObjectForMessage clonedField13(ObjectForMessage field13) {
        if (field13 != null) {
            ObjectForMessage newField13 = new ObjectForMessage();
            if (field13.getData() != null) {
                newField13.setData(new ArrayList<>(field13.getData()));
            }
            return newField13;
        }
        return null;
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.of(messages.get(id));
    }
}
