package ru.yandex.practicum.filmorate.exception;

/**
 * Исключение, выбрасываемое при ошибках валидации данных.
 */
public class ValidationException extends RuntimeException {
    /**
     * Создаёт исключение с сообщением об ошибке.
     * @param message сообщение об ошибке
     */
    public ValidationException(final String message) {
        super(message);
    }
}
