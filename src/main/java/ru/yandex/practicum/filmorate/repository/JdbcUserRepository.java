package ru.yandex.practicum.filmorate.repository;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
public class JdbcUserRepository implements UserRepository {

    private static final String INSERT_USER_SQL =
            "INSERT INTO users (email, login, name, birthday) VALUES (:email, :login, :name, :birthday)";

    private static final String UPDATE_USER_SQL =
            "UPDATE users SET email = :email, login = :login, name = :name, birthday = :birthday WHERE id = :id";

    private static final String SELECT_USER_BY_ID_SQL =
            "SELECT * FROM users WHERE id = :id";

    private static final String SELECT_ALL_USERS_SQL =
            "SELECT * FROM users";

    private static final String ADD_FRIEND_SQL =
            "MERGE INTO friendships (user_id, friend_id, status) VALUES (:userId, :friendId, :status)";

    private static final String REMOVE_FRIEND_SQL =
            "DELETE FROM friendships WHERE user_id = :userId AND friend_id = :friendId";

    private static final String SELECT_FRIENDS_SQL =
            "SELECT u.* FROM users u " +
            "JOIN friendships f ON u.id = f.friend_id " +
            "WHERE f.user_id = :userId AND f.status = :status";

    private static final String SELECT_COMMON_FRIENDS_SQL =
            "SELECT u.* FROM users u " +
            "JOIN friendships f1 ON u.id = f1.friend_id " +
            "JOIN friendships f2 ON u.id = f2.friend_id " +
            "WHERE f1.user_id = :userId1 AND f2.user_id = :userId2 " +
            "AND f1.status = :status AND f2.status = :status";

    private static final String SELECT_USER_FRIENDS_SQL =
            "SELECT friend_id FROM friendships WHERE user_id = :userId";

    private final NamedParameterJdbcOperations jdbc;
    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    };

    public JdbcUserRepository(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public User save(User user) {
        log.debug("Сохранение пользователя: {}", user);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(INSERT_USER_SQL, params, keyHolder, new String[]{"id"});

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new RuntimeException("Не удалось получить сгенерированный идентификатор пользователя");
        }
        int id = key.intValue();
        user.setId(id);
        log.debug("Пользователь сохранён: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        log.debug("Обновление пользователя: {}", user);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday())
                .addValue("id", user.getId());

        int updatedRows = jdbc.update(UPDATE_USER_SQL, params);
        if (updatedRows == 0) {
            log.warn("Пользователь не найден id={}", user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Пользователь с id = " + user.getId() + " не найден");
        }
        log.debug("Пользователь обновлён: {}", user);
        return user;
    }

    @Override
    public Optional<User> findById(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        List<User> users = jdbc.query(SELECT_USER_BY_ID_SQL, params, USER_ROW_MAPPER);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        User user = users.getFirst();
        user.setFriends(loadFriends(user.getId()));
        return Optional.of(user);
    }

    @Override
    public List<User> findAll() {
        List<User> users = jdbc.query(SELECT_ALL_USERS_SQL, USER_ROW_MAPPER);
        for (User user : users) {
            user.setFriends(loadFriends(user.getId()));
        }
        return users;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        log.debug("Добавление в друзья: userId={}, friendId={}", userId, friendId);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("friendId", friendId)
                .addValue("status", FriendshipStatus.CONFIRMED.name());
        jdbc.update(ADD_FRIEND_SQL, params);
        log.debug("Друг добавлен");
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        log.debug("Удаление из друзей: userId={}, friendId={}", userId, friendId);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("friendId", friendId);
        jdbc.update(REMOVE_FRIEND_SQL, params);
        log.debug("Друг удалён");
    }

    @Override
    public List<User> getFriends(int userId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("status", FriendshipStatus.CONFIRMED.name());
        return jdbc.query(SELECT_FRIENDS_SQL, params, USER_ROW_MAPPER);
    }

    @Override
    public List<User> getCommonFriends(int userId1, int userId2) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId1", userId1)
                .addValue("userId2", userId2)
                .addValue("status", FriendshipStatus.CONFIRMED.name());
        return jdbc.query(SELECT_COMMON_FRIENDS_SQL, params, USER_ROW_MAPPER);
    }

    private Set<Integer> loadFriends(int userId) {
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return new HashSet<>(jdbc.query(SELECT_USER_FRIENDS_SQL, params, (rs, rowNum) -> rs.getInt("friend_id")));
    }


}