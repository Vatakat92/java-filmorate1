package ru.yandex.practicum.filmorate.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldThrowBadRequestIfValidationException() {
        ValidationException ex = new ValidationException("Ошибка валидации");
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> handler.handleValidationException(ex));
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
        assertEquals("400 BAD_REQUEST \"Ошибка валидации\"", thrown.getMessage());
    }

    @Test
    void shouldThrowNotFoundIfNoSuchElementException() {
        java.util.NoSuchElementException ex = new java.util.NoSuchElementException("Не найдено");
        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> handler.handleNotFoundException(ex));
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
        assertEquals("404 NOT_FOUND \"Не найдено\"", thrown.getMessage());
    }
}
