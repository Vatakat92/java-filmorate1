package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMpaRepository implements MpaRepository {

    private static final String SELECT_MPA_BY_ID_SQL = "SELECT * FROM mpa_ratings WHERE id = ?";
    private static final String SELECT_ALL_MPA_SQL = "SELECT * FROM mpa_ratings ORDER BY id";

    private final JdbcTemplate jdbcTemplate;

    public JdbcMpaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Mpa> findById(int id) {
        List<Mpa> mpaList = jdbcTemplate.query(SELECT_MPA_BY_ID_SQL, this::mapRowToMpa, id);
        return mpaList.isEmpty() ? Optional.empty() : Optional.of(mpaList.getFirst());
    }

    @Override
    public List<Mpa> findAll() {
        return jdbcTemplate.query(SELECT_ALL_MPA_SQL, this::mapRowToMpa);
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    }
}