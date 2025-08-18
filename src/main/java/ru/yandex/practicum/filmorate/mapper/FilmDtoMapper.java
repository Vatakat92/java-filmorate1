package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FilmDtoMapper {

    public Film toDomain(FilmCreateRequest req) {
        Film f = new Film();
        f.setName(req.getName());
        f.setDescription(req.getDescription());
        f.setReleaseDate(req.getReleaseDate());
        f.setDuration(req.getDuration());

        Optional.ofNullable(req.getMpa())
                .ifPresent(mr -> {
                    Mpa m = new Mpa();
                    m.setId(mr.getId());
                    f.setMpaRating(m);
                });

        f.setGenres(mapGenreIds(req.getGenres()));
        return f;
    }

    public Film toDomain(FilmUpdateRequest req) {
        Film f = new Film();
        f.setId(req.getId());
        f.setName(req.getName());
        f.setDescription(req.getDescription());
        f.setReleaseDate(req.getReleaseDate());
        f.setDuration(req.getDuration());

        Optional.ofNullable(req.getMpa())
                .ifPresent(mr -> {
                    Mpa m = new Mpa();
                    m.setId(mr.getId());
                    f.setMpaRating(m);
                });

        f.setGenres(mapGenreIds(req.getGenres()));
        return f;
    }

    public FilmResponse toResponse(Film f) {
        FilmResponse dto = new FilmResponse();
        dto.setId(f.getId());
        dto.setName(f.getName());
        dto.setDescription(f.getDescription());
        dto.setReleaseDate(f.getReleaseDate());
        dto.setDuration(f.getDuration());
        if (f.getMpaRating() != null) {
            MpaDto m = new MpaDto();
            m.setId(f.getMpaRating().getId());
            m.setName(f.getMpaRating().getName());
            m.setDescription(f.getMpaRating().getDescription());
            dto.setMpa(m);
        }
        dto.setGenres(f.getGenres().stream().map(g -> {
            GenreDto gd = new GenreDto();
            gd.setId(g.getId());
            gd.setName(g.getName());
            gd.setDescription(g.getDescription());
            return gd;
        }).toList());
        return dto;
    }

    public List<FilmResponse> toResponseList(List<Film> films) {
        return films.stream().map(this::toResponse).toList();
    }

    private LinkedHashSet<Genre> mapGenreIds(List<FilmCreateRequest.IdOnly> ids) {
        if (ids == null || ids.isEmpty()) {
            return new LinkedHashSet<>();
        }
        return ids.stream()
                .map(g -> {
                    Genre genre = new Genre();
                    genre.setId(g.getId());
                    return genre;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
