package ru.practicum.shareit.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class ConditionsNotMetException extends RuntimeException {
    public ConditionsNotMetException(String message) {
        super(message);
    }
}
