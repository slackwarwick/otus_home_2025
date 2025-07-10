package ru.otus.processor;

import org.junit.jupiter.api.Test;
import ru.otus.model.Message;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


class ProcessorThrowEvenSecTest {
    @Test
    public void testProcess() {
        final Processor p1 = new ProcessorThrowEvenSec(() -> LocalDateTime.of(
                2025, 7, 10, 10, 10, 10
        ));
        final Message m1 = new Message.Builder(1L).build();
        assertThrows(IllegalStateException.class, () -> p1.process(m1));

        final Processor p2 = new ProcessorThrowEvenSec(() -> LocalDateTime.of(
                2025, 7, 10, 10, 10, 11
        ));
        final Message m2 = new Message.Builder(2L).build();
        assertDoesNotThrow(() -> p2.process(m2));
        assertEquals(m2, p2.process(m2));
    }



}