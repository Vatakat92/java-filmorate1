package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class JdbcGenreRepository implements GenreRepository {

    private static final String SELECT_GENRE_BY_ID_SQL = "SELECT id, name, description FROM genres WHERE id = ?";
    private static final String SELECT_ALL_GENRES_SQL = "SELECT id, name, description FROM genres ORDER BY id";
    private static final String SELECT_GENRES_BY_IDS_SQL = "SELECT id, name, description FROM genres WHERE id IN (%s) ORDER BY id";

    private final JdbcTemplate jdbcTemplate;

    public JdbcGenreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Genre> findById(int id) {
        log.debug("Поиск рейтинга MPA по id: {}", id);
        List<Genre> genres = jdbcTemplate.query(SELECT_GENRE_BY_ID_SQL, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            genre.setDescription(rs.getString("description"));
            return genre;
        }, id);
        return genres.isEmpty() ? Optional.empty() : Optional.of(genres.getFirst());
    }

    @Override
    public List<Genre> findAll() {
        log.debug("Find all genres");
        return jdbcTemplate.query(SELECT_ALL_GENRES_SQL, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            genre.setDescription(rs.getString("description"));
            return genre;
        });
    }

    @Override
    public List<Genre> findByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        log.debug("Find genres by ids: {}", ids);
        String placeholders = String.join(",", ids.stream().map(id -> "?").toArray(String[]::new));
        String sql = String.format(SELECT_GENRES_BY_IDS_SQL, placeholders);
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            genre.setDescription(rs.getString("description"));
            return genre;
        }, ids.toArray());
    }
}