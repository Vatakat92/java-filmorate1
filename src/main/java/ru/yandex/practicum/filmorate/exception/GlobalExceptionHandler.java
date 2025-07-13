package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ru.yandex.practicum.filmorate.exception.ValidationException.class)
    public void handleValidationException(ru.yandex.practicum.filmorate.exception.ValidationException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({java.util.NoSuchElementException.class})
    public void handleNotFoundException(java.util.NoSuchElementException ex) {
        log.warn("Not found: {}", ex.getMessage());
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> java.util.Objects.requireNonNullElse(fieldError.getDefaultMessage(), "Ошибка валидации"))
                .findFirst()
                .orElse("Ошибка валидации");
        log.warn("Validation error (invalid argument): {}", message);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public void handleResponseStatusException(ResponseStatusException ex) {
        throw ex;
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleOtherExceptions(Throwable ex) {
        log.error("Internal server error", ex);
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера: " + ex.getMessage());
    }
}