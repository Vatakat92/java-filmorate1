package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

/**
 * Модель пользователя.
 * Содержит данные о пользователе и аннотации для валидации.
 */
@Data
public class User {
    /**
     * Уникальный идентификатор пользователя.
     */
    private int id;
    /**
     * Электронная почта пользователя.
     */
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email должен быть корректным")
    private String email;
    /**
     * Логин пользователя (не может быть пустым и содержать пробелы).
     */
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелы")
    private String login;
    /**
     * Имя пользователя (может быть пустым).
     */
    private String name;
    /**
     * Дата рождения пользователя (не может быть в будущем).
     */
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
