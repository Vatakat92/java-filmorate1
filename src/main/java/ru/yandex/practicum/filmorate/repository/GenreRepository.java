package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {
    Optional<Genre> findById(int id);

    List<Genre> findAll();

    List<Genre> findByIds(List<Integer> ids);
}