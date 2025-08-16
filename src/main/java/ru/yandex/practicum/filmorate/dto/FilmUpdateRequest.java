package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ReleaseDateValid;

import java.time.LocalDate;
import java.util.List;

@Data
public class FilmUpdateRequest {
    @NotNull
    private Integer id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    public static final int MAX_DESCRIPTION_LENGTH = 200;
    @Size(max = MAX_DESCRIPTION_LENGTH,
          message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    @ReleaseDateValid
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;

    private FilmCreateRequest.IdOnly mpa;

    private List<FilmCreateRequest.IdOnly> genres;
}