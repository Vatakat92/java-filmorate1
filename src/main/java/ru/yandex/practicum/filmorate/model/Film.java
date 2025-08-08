package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ReleaseDateValid;

@Data
public class Film {
    private int id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    @Size(max = MAX_DESCRIPTION_LENGTH,
          message = "Максимальная длина описания — 200 символов")
    private String description;
    @NotNull(message = "Дата релиза обязательна")
    @ReleaseDateValid
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть "
            + "положительным числом")
    private int duration;
    private Set<Integer> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    @JsonProperty("mpa")
    private Mpa mpaRating;
}
