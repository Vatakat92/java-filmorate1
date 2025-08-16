package ru.yandex.practicum.filmorate.exception;

import java.io.Serial;

public class ValidationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ValidationException(final String message) {
        super(message);
    }
}
