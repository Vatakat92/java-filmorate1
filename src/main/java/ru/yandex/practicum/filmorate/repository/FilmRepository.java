package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmRepository {
    Film save(Film film);

    Film update(Film film);

    Optional<Film> findById(int id);

    List<Film> findAll();

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> findMostPopular(int count);
}