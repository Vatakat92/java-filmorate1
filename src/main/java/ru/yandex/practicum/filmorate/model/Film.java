package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Data;

/**
 * Модель фильма.
 * Содержит данные о фильме и аннотации для валидации.
 */
@Data
public class Film {
    /**
     * Уникальный идентификатор фильма.
     */
    private int id;
    /**
     * Название фильма (не может быть пустым).
     */
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    /**
     * Максимальная длина описания фильма.
     */
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    /**
     * Описание фильма (максимум 200 символов).
     */
    @Size(max = MAX_DESCRIPTION_LENGTH,
          message = "Максимальная длина описания — 200 символов")
    private String description;
    /**
     * Дата релиза фильма (обязательна, валидируется отдельно).
     */
    @NotNull(message = "Дата релиза обязательна")
    @ReleaseDateValid
    private LocalDate releaseDate;
    /**
     * Продолжительность фильма (только положительное число).
     */
    @Positive(message = "Продолжительность фильма должна быть "
            + "положительным числом")
    private int duration;
}
