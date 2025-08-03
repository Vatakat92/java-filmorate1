package ru.yandex.practicum.filmorate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.MpaRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.HashSet;
import java.util.List;

@Service
public class BaseFilmService implements FilmService {
    protected final FilmRepository filmRepository;
    protected final UserRepository userRepository;
    protected final MpaRepository mpaRepository;
    protected final GenreRepository genreRepository;

    public BaseFilmService(FilmRepository filmRepository, UserRepository userRepository,
                          MpaRepository mpaRepository, GenreRepository genreRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.mpaRepository = mpaRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    public Film createFilm(Film film) {
        if (film.getMpaRating() != null) {
            mpaRepository.findById(film.getMpaRating().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "MPA рейтинг с id = " + film.getMpaRating().getId() + " не найден"));
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            final List<Integer> genreIds = film.getGenres().stream().map(Genre::getId).toList();
            final List<Genre> genres = genreRepository.findByIds(genreIds);

            if (genreIds.size() != genres.size()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Жанры не найдены");
            }
        }

        return filmRepository.save(film);
    }

    @Override
    public Film updateFilm(Film film) {
        final Film f = filmRepository.findById(film.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Фильм с id = " + film.getId() + " не найден"));

        final Mpa mpa = mpaRepository.findById(film.getMpaRating().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "MPA рейтинг с id = " + film.getMpaRating().getId() + " не найден"));

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            final List<Integer> genreIds = film.getGenres().stream().map(Genre::getId).toList();
            final List<Genre> genres = genreRepository.findByIds(genreIds);

            if (genreIds.size() != genres.size()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Жанры не найдены");
            }
            f.setGenres(new HashSet<>(genres));
        } else {
            f.setGenres(new HashSet<>());
        }

        f.setName(film.getName());
        f.setDescription(film.getDescription());
        f.setReleaseDate(film.getReleaseDate());
        f.setDuration(film.getDuration());
        f.setMpaRating(mpa);

        return filmRepository.update(f);
    }

    @Override
    public Film getFilmById(int id) {
        return filmRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с id = " + id + " не найден"));
    }

    @Override
    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    @Override
    public void addLike(int filmId, int userId) {
        if (filmRepository.findById(filmId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с id = " + filmId + " не найден");
        }
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id = " + userId + " не найден");
        }
        filmRepository.addLike(filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        if (filmRepository.findById(filmId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с id = " + filmId + " не найден");
        }
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id = " + userId + " не найден");
        }
        filmRepository.removeLike(filmId, userId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return filmRepository.findMostPopular(count);
    }
}