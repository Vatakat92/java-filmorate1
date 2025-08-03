package ru.yandex.practicum.filmorate.repository;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.mapper.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
public class JdbcFilmRepository implements FilmRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcFilmRepository.class);

    private final NamedParameterJdbcOperations jdbc;
    private final FilmResultSetExtractor filmResultSetExtractor;

    public JdbcFilmRepository(NamedParameterJdbcOperations jdbc, FilmResultSetExtractor filmResultSetExtractor) {
        this.jdbc = jdbc;
        this.filmResultSetExtractor = filmResultSetExtractor;
    }

    @Override
    public Film save(Film film) {
        log.debug("Saving film: {}", film);
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) " +
                    "VALUES (:name, :description, :releaseDate, :duration, :mpaRatingId)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpaRatingId", film.getMpaRating().getId());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new RuntimeException("Failed to get generated key for film");
        }
        int id = key.intValue();
        film.setId(id);

        saveGenres(film);
        log.debug("Film saved: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.debug("Updating film: {}", film);
        String sql = "UPDATE films SET name = :name, description = :description, release_date = :releaseDate, " +
                    "duration = :duration, mpa_rating_id = :mpaRatingId WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpaRatingId", film.getMpaRating().getId())
                .addValue("id", film.getId());

        int updatedRows = jdbc.update(sql, params);
        if (updatedRows == 0) {
            log.warn("Film with id {} not found", film.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Фильм с id = " + film.getId() + " не найден");
        }
        saveGenres(film);
        log.debug("Film updated: {}", film);

        film.setGenres(loadGenres(film.getId()));
        film.setLikes(loadLikes(film.getId()));

        return film;
    }

    @Override
    public Optional<Film> findById(int id) {
        String sql = "SELECT f.*, m.name AS mpa_name, " +
                    "g.id AS genre_id, g.name AS genre_name, " +
                    "fl.user_id AS like_user_id " +
                    "FROM films f " +
                    "LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id " +
                    "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
                    "LEFT JOIN genres g ON fg.genre_id = g.id " +
                    "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                    "WHERE f.id = :id " +
                    "ORDER BY g.id, fl.user_id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        List<Film> films = jdbc.query(sql, params, filmResultSetExtractor);
        if (films == null || films.isEmpty()) {
            return Optional.empty();
        }
        Film film = films.getFirst();
        film.setGenres(loadGenres(film.getId()));
        film.setLikes(loadLikes(film.getId()));
        return Optional.of(film);
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT f.*, m.name AS mpa_name, " +
                    "g.id AS genre_id, g.name AS genre_name, " +
                    "fl.user_id AS like_user_id " +
                    "FROM films f " +
                    "LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id " +
                    "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
                    "LEFT JOIN genres g ON fg.genre_id = g.id " +
                    "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                    "ORDER BY f.id, g.id, fl.user_id";
        List<Film> films = jdbc.query(sql, filmResultSetExtractor);
        if (films != null) {
            for (Film film : films) {
                film.setGenres(loadGenres(film.getId()));
                film.setLikes(loadLikes(film.getId()));
            }
        }
        return films != null ? films : new ArrayList<>();
    }

    @Override
    public void addLike(int filmId, int userId) {
        log.debug("Adding like to film {} from user {}", filmId, userId);
        String sql = "MERGE INTO film_likes (film_id, user_id) VALUES (:filmId, :userId)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("userId", userId);
        jdbc.update(sql, params);
        log.debug("Like added");
    }

    @Override
    public void removeLike(int filmId, int userId) {
        log.debug("Removing like from film {} by user {}", filmId, userId);
        String sql = "DELETE FROM film_likes WHERE film_id = :filmId AND user_id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("userId", userId);
        jdbc.update(sql, params);
        log.debug("Like removed");
    }

    @Override
    public List<Film> findMostPopular(int count) {
        String sql = "SELECT f.*, m.name AS mpa_name, " +
                "g.id AS genre_id, g.name AS genre_name, " +
                "fl.user_id AS like_user_id " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id " +
                "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.id " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "LEFT JOIN (SELECT film_id, COUNT(user_id) as likes_count FROM film_likes GROUP BY film_id) AS popular " +
                "ON f.id = popular.film_id " +
                "ORDER BY popular.likes_count DESC, f.id ASC " +
                "LIMIT :count";

        MapSqlParameterSource params = new MapSqlParameterSource("count", count);
        return jdbc.query(sql, params, filmResultSetExtractor);
    }

    private void saveGenres(Film film) {
        String deleteSql = "DELETE FROM film_genres WHERE film_id = :filmId";
        MapSqlParameterSource deleteParams = new MapSqlParameterSource("filmId", film.getId());
        jdbc.update(deleteSql, deleteParams);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String insertSql = "MERGE INTO film_genres (film_id, genre_id) KEY(film_id, genre_id) VALUES (:filmId, :genreId)";

            MapSqlParameterSource[] batchParams = film.getGenres().stream()
                    .map(genre -> new MapSqlParameterSource()
                            .addValue("filmId", film.getId())
                            .addValue("genreId", genre.getId()))
                    .toArray(MapSqlParameterSource[]::new);

            jdbc.batchUpdate(insertSql, batchParams);
        }
    }

    private Set<Genre> loadGenres(int filmId) {
        String sql = "SELECT g.id, g.name FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = :filmId";
        MapSqlParameterSource params = new MapSqlParameterSource("filmId", filmId);
        return new LinkedHashSet<>(jdbc.query(sql, params, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }));
    }

    private Set<Integer> loadLikes(int filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = :filmId";
        MapSqlParameterSource params = new MapSqlParameterSource("filmId", filmId);
        return new HashSet<>(jdbc.query(sql, params, (rs, rowNum) -> rs.getInt("user_id")));
    }
}