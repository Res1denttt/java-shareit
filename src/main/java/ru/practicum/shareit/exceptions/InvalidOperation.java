package ru.practicum.shareit.exceptions;

public class InvalidOperation extends RuntimeException {
    public InvalidOperation(String message) {
        super(message);
    }
}
