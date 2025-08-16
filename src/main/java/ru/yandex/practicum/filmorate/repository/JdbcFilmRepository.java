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

    private static final String INSERT_FILM_SQL =
            "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) " +
            "VALUES (:name, :description, :releaseDate, :duration, :mpaRatingId)";

    private static final String UPDATE_FILM_SQL =
            "UPDATE films SET name = :name, description = :description, release_date = :releaseDate, " +
            "duration = :duration, mpa_rating_id = :mpaRatingId WHERE id = :id";

    private static final String SELECT_FILMS_BASE_SQL =
            "SELECT f.*, m.name AS mpa_name, " +
            "g.id AS genre_id, g.name AS genre_name, " +
            "fl.user_id AS like_user_id " +
            "FROM films f " +
            "LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id " +
            "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
            "LEFT JOIN genres g ON fg.genre_id = g.id " +
            "LEFT JOIN film_likes fl ON f.id = fl.film_id ";

    private static final String SELECT_MOST_POPULAR_FILMS_SQL =
            SELECT_FILMS_BASE_SQL +
            "LEFT JOIN (SELECT film_id, COUNT(user_id) as likes_count FROM film_likes GROUP BY film_id) AS popular " +
            "ON f.id = popular.film_id " +
            "ORDER BY popular.likes_count DESC, f.id ASC " +
            "LIMIT :count";

    private static final String ADD_LIKE_SQL =
            "MERGE INTO film_likes (film_id, user_id) VALUES (:filmId, :userId)";

    private static final String REMOVE_LIKE_SQL =
            "DELETE FROM film_likes WHERE film_id = :filmId AND user_id = :userId";

    private static final String DELETE_FILM_GENRES_SQL =
            "DELETE FROM film_genres WHERE film_id = :filmId";

    private static final String INSERT_FILM_GENRE_SQL =
            "MERGE INTO film_genres (film_id, genre_id) KEY(film_id, genre_id) VALUES (:filmId, :genreId)";

    private static final String SELECT_FILM_GENRES_SQL =
            "SELECT g.id, g.name FROM genres g " +
            "JOIN film_genres fg ON g.id = fg.genre_id " +
            "WHERE fg.film_id = :filmId";

    private static final String SELECT_FILM_LIKES_SQL =
            "SELECT user_id FROM film_likes WHERE film_id = :filmId";

    private final NamedParameterJdbcOperations jdbc;
    private final FilmResultSetExtractor filmResultSetExtractor;

    public JdbcFilmRepository(NamedParameterJdbcOperations jdbc, FilmResultSetExtractor filmResultSetExtractor) {
        this.jdbc = jdbc;
        this.filmResultSetExtractor = filmResultSetExtractor;
    }

    @Override
    public Film save(Film film) {
        log.debug("Сохранение фильма: {}", film);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpaRatingId", film.getMpaRating().getId());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(INSERT_FILM_SQL, params, keyHolder, new String[]{"id"});

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new RuntimeException("Не удалось получить сгенерированный идентификатор фильма");
        }
        int id = key.intValue();
        film.setId(id);

        saveGenres(film);
        log.debug("Фильм сохранён: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.debug("Обновление фильма: {}", film);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpaRatingId", film.getMpaRating().getId())
                .addValue("id", film.getId());

        int updatedRows = jdbc.update(UPDATE_FILM_SQL, params);
        if (updatedRows == 0) {
            log.warn("Фильм не найден id={}", film.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Фильм с id = " + film.getId() + " не найден");
        }
        saveGenres(film);
        log.debug("Фильм обновлён: {}", film);

        film.setGenres(loadGenres(film.getId()));
        film.setLikes(loadLikes(film.getId()));

        return film;
    }

    @Override
    public Optional<Film> findById(int id) {
        List<Film> films = findFilmsWithCondition("WHERE f.id = :id",
                new MapSqlParameterSource("id", id));
        return films.isEmpty() ? Optional.empty() : Optional.of(films.getFirst());
    }

    @Override
    public List<Film> findAll() {
        return findFilmsWithCondition("", new MapSqlParameterSource());
    }

    private List<Film> findFilmsWithCondition(String whereClause, MapSqlParameterSource params) {
        String sql = SELECT_FILMS_BASE_SQL + whereClause + " ORDER BY f.id, g.id, fl.user_id";
        return jdbc.query(sql, params, filmResultSetExtractor);
    }

    @Override
    public void addLike(int filmId, int userId) {
        log.debug("Добавление лайка к фильму {} от пользователя {}", filmId, userId);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("userId", userId);
        jdbc.update(ADD_LIKE_SQL, params);
        log.debug("Лайк добавлен");
    }

    @Override
    public void removeLike(int filmId, int userId) {
        log.debug("Удаление лайка у фильма {} пользователем {}", filmId, userId);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("userId", userId);
        jdbc.update(REMOVE_LIKE_SQL, params);
        log.debug("Лайк удалён");
    }

    @Override
    public List<Film> findMostPopular(int count) {
        MapSqlParameterSource params = new MapSqlParameterSource("count", count);
        return jdbc.query(SELECT_MOST_POPULAR_FILMS_SQL, params, filmResultSetExtractor);
    }

    private void saveGenres(Film film) {
        MapSqlParameterSource deleteParams = new MapSqlParameterSource("filmId", film.getId());
        jdbc.update(DELETE_FILM_GENRES_SQL, deleteParams);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            MapSqlParameterSource[] batchParams = film.getGenres().stream()
                    .map(genre -> new MapSqlParameterSource()
                            .addValue("filmId", film.getId())
                            .addValue("genreId", genre.getId()))
                    .toArray(MapSqlParameterSource[]::new);

            jdbc.batchUpdate(INSERT_FILM_GENRE_SQL, batchParams);
        }
    }

    private Set<Genre> loadGenres(int filmId) {
        MapSqlParameterSource params = new MapSqlParameterSource("filmId", filmId);
        return new LinkedHashSet<>(jdbc.query(SELECT_FILM_GENRES_SQL, params, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }));
    }

    private Set<Integer> loadLikes(int filmId) {
        MapSqlParameterSource params = new MapSqlParameterSource("filmId", filmId);
        return new HashSet<>(jdbc.query(SELECT_FILM_LIKES_SQL, params, (rs, rowNum) -> rs.getInt("user_id")));
    }
}