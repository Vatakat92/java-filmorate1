package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreRepository genreRepository;

    @GetMapping
    public List<Genre> getAllGenres() {
        log.debug("Запрос всех жанров");
        return genreRepository.findAll();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        log.debug("Запрос жанра id={}", id);
        return genreRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Жанр с id = " + id + " не найден"));
    }
}
