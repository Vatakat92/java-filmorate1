package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcGenreRepository implements GenreRepository {

    private static final String SELECT_GENRE_BY_ID_SQL = "SELECT * FROM genres WHERE id = ?";
    private static final String SELECT_ALL_GENRES_SQL = "SELECT * FROM genres ORDER BY id";
    private static final String SELECT_GENRES_BY_IDS_SQL = "SELECT * FROM genres WHERE id IN (%s) ORDER BY id";

    private final JdbcTemplate jdbcTemplate;

    public JdbcGenreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Genre> findById(int id) {
        List<Genre> genres = jdbcTemplate.query(SELECT_GENRE_BY_ID_SQL, this::mapRowToGenre, id);
        return genres.isEmpty() ? Optional.empty() : Optional.of(genres.getFirst());
    }

    @Override
    public List<Genre> findAll() {
        return jdbcTemplate.query(SELECT_ALL_GENRES_SQL, this::mapRowToGenre);
    }

    @Override
    public List<Genre> findByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        String placeholders = String.join(",", ids.stream().map(id -> "?").toArray(String[]::new));
        String sql = String.format(SELECT_GENRES_BY_IDS_SQL, placeholders);
        return jdbcTemplate.query(sql, this::mapRowToGenre, ids.toArray());
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("id"));
        genre.setName(rs.getString("name"));
        return genre;
    }
}