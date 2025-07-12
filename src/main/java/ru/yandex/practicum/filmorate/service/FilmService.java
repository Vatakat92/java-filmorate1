package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public void addLike(int filmId, int userId) {
        try {
            Film film = getFilmOrThrow(filmId);
            userService.getUserById(userId);
            film.getLikes().add(userId);
        } catch (NoSuchElementException e) {
            String message = e.getMessage();
            if (message != null && message.contains("Пользователь")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id = " + userId + " не найден");
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с id = " + filmId + " не найден");
            }
        }
    }

    public void removeLike(int filmId, int userId) {
        try {
            Film film = getFilmOrThrow(filmId);
            userService.getUserById(userId);
            film.getLikes().remove(userId);
        } catch (NoSuchElementException e) {
            String message = e.getMessage();
            if (message != null && message.contains("Пользователь")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id = " + userId + " не найден");
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с id = " + filmId + " не найден");
            }
        }
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.containsFilm(film.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с id = " + film.getId() + " не найден");
        }
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(int id) {
        try {
            return getFilmOrThrow(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с id = " + id + " не найден");
        }
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    private Film getFilmOrThrow(int id) {
        return filmStorage.getAllFilms().stream()
                .filter(f -> f.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Фильм с id = " + id + " не найден"));
    }
}
