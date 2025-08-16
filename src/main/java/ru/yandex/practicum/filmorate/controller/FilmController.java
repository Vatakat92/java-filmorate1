package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmCreateRequest;
import ru.yandex.practicum.filmorate.dto.FilmResponse;
import ru.yandex.practicum.filmorate.dto.FilmUpdateRequest;
import ru.yandex.practicum.filmorate.mapper.FilmDtoMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public final class FilmController {
    private final FilmService filmService;
    private final FilmDtoMapper filmDtoMapper;

    @PostMapping
    public ResponseEntity<FilmResponse> addFilm(@Valid @RequestBody final FilmCreateRequest request) {
        Film createdFilm = filmService.createFilm(filmDtoMapper.toDomain(request));
        FilmResponse response = filmDtoMapper.toResponse(createdFilm);
        log.info("Создан фильм: {}", response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<FilmResponse> updateFilm(@Valid @RequestBody final FilmUpdateRequest request) {
        Film updatedFilm = filmService.updateFilm(filmDtoMapper.toDomain(request));
        FilmResponse response = filmDtoMapper.toResponse(updatedFilm);
        log.info("Обновлен фильм: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public List<FilmResponse> getAllFilms() {
        log.info("Запрос всех фильмов");
        return filmDtoMapper.toResponseList(filmService.getAllFilms());
    }

    @GetMapping("/{id}")
    public FilmResponse getFilmById(@PathVariable int id) {
        log.info("Запрос фильма id={}", id);
        return filmDtoMapper.toResponse(filmService.getFilmById(id));
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Добавление лайка: filmId={}, userId={}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Удаление лайка: filmId={}, userId={}", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<FilmResponse> getPopularFilms(
            @RequestParam(defaultValue = "10") int count) {
        log.info("Запрос популярных фильмов: count={}", count);
        return filmDtoMapper.toResponseList(filmService.getTopFilms(count));
    }
}
