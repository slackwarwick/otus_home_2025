package ru.otus.listener.homework;

import java.util.*;
import ru.otus.listener.Listener;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;

public class HistoryListener implements Listener, HistoryReader {

    Set<Message> messages = new HashSet<>();

    @Override
    public void onUpdated(Message msg) {
        ObjectForMessage field13 = new ObjectForMessage();
        field13.setData(new ArrayList<>(msg.getField13().getData()));
        messages.add(msg.toBuilder().field13(field13).build());
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return messages.stream().filter(m -> m.getId() == id).findFirst();
    }
}
