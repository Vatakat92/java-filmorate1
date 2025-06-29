package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер для управления фильмами.
 * Позволяет добавлять, обновлять и получать список фильмов.
 */
@RestController
@RequestMapping("/films")
public final class FilmController {
    /**
     * Список всех фильмов в памяти приложения.
     */
    private final List<Film> films = new ArrayList<>();
    private int filmIdCounter = 1;

    /**
     * Добавляет новый фильм.
     * @param film добавляемый фильм
     * @return добавленный фильм
     * @throws IllegalArgumentException если фильм с таким id уже существует
     */
    @PostMapping
    public Film addFilm(@Valid @RequestBody final Film film) {
        film.setId(filmIdCounter++);
        films.add(film);
        return film;
    }

    /**
     * Обновляет существующий фильм.
     * @param film обновляемый фильм
     * @return обновленный фильм
     * @throws IllegalArgumentException если фильм не найден
     */
    @PutMapping
    public Film updateFilm(@Valid @RequestBody final Film film) {
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == film.getId()) {
                films.set(i, film);
                return film;
            }
        }
        throw new IllegalArgumentException(
            "Фильм с id=" + film.getId() + " не найден");
    }

    /**
     * Возвращает список всех фильмов.
     * @return список фильмов
     */
    @GetMapping
    public List<Film> getAllFilms() {
        return films;
    }
}
