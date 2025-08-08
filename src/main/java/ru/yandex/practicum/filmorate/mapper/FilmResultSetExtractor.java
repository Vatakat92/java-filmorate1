package ru.yandex.practicum.filmorate.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class   FilmResultSetExtractor implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Film> filmMap = new LinkedHashMap<>();

        while (rs.next()) {
            int filmId = rs.getInt("id");
            Film film = filmMap.get(filmId);

            if (film == null) {
                film = new Film();
                film.setId(filmId);
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(rs.getInt("duration"));

                int mpaId = rs.getInt("mpa_rating_id");
                if (!rs.wasNull()) {
                    Mpa mpa = new Mpa();
                    mpa.setId(mpaId);
                    mpa.setName(rs.getString("mpa_name"));
                    film.setMpaRating(mpa);
                }

                film.setGenres(new LinkedHashSet<>());
                film.setLikes(new HashSet<>());
                filmMap.put(filmId, film);
            }

            int genreId = rs.getInt("genre_id");
            if (!rs.wasNull()) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(rs.getString("genre_name"));
                film.getGenres().add(genre);
            }

            int likeUserId = rs.getInt("like_user_id");
            if (!rs.wasNull()) {
                film.getLikes().add(likeUserId);
            }
        }

        return new ArrayList<>(filmMap.values());
    }
}
