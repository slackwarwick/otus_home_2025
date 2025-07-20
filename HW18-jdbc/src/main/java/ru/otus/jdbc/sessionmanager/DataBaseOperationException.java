package ru.otus.jdbc.sessionmanager;

public class DataBaseOperationException extends RuntimeException {
    public DataBaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
