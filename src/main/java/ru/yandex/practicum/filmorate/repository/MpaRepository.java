package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaRepository {
    Optional<Mpa> findById(int id);

    List<Mpa> findAll();
}