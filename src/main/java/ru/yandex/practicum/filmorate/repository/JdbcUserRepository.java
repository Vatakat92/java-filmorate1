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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class JdbcUserRepository implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcUserRepository.class);

    private final NamedParameterJdbcOperations jdbc;

    public JdbcUserRepository(NamedParameterJdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public User save(User user) {
        log.debug("Saving user: {}", user);
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (:email, :login, :name, :birthday)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder, new String[]{"id"});

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new RuntimeException("Failed to get generated key for user");
        }
        int id = key.intValue();
        user.setId(id);
        log.debug("User saved: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        log.debug("Updating user: {}", user);
        String sql = "UPDATE users SET email = :email, login = :login, name = :name, birthday = :birthday WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday())
                .addValue("id", user.getId());

        int updatedRows = jdbc.update(sql, params);
        if (updatedRows == 0) {
            log.warn("User with id {} not found", user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Пользователь с id = " + user.getId() + " не найден");
        }
        log.debug("User updated: {}", user);
        return user;
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        List<User> users = jdbc.query(sql, params, this::mapRowToUser);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        User user = users.getFirst();
        user.setFriends(loadFriends(user.getId()));
        return Optional.of(user);
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> users = jdbc.query(sql, this::mapRowToUser);
        for (User user : users) {
            user.setFriends(loadFriends(user.getId()));
        }
        return users;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        log.debug("Adding friend {} to user {}", friendId, userId);
        String sql = "MERGE INTO friendships (user_id, friend_id, status) VALUES (:userId, :friendId, :status)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("friendId", friendId)
                .addValue("status", FriendshipStatus.CONFIRMED.name());
        jdbc.update(sql, params);
        log.debug("Friend added");
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        log.debug("Removing friend {} from user {}", friendId, userId);
        String sql = "DELETE FROM friendships WHERE user_id = :userId AND friend_id = :friendId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("friendId", friendId);
        jdbc.update(sql, params);
        log.debug("Friend removed");
    }

    @Override
    public List<User> getFriends(int userId) {
        String sql = "SELECT u.* FROM users u " +
                    "JOIN friendships f ON u.id = f.friend_id " +
                    "WHERE f.user_id = :userId AND f.status = :status";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("status", FriendshipStatus.CONFIRMED.name());
        return jdbc.query(sql, params, this::mapRowToUser);
    }

    @Override
    public List<User> getCommonFriends(int userId1, int userId2) {
        String sql = "SELECT u.* FROM users u " +
                    "JOIN friendships f1 ON u.id = f1.friend_id " +
                    "JOIN friendships f2 ON u.id = f2.friend_id " +
                    "WHERE f1.user_id = :userId1 AND f2.user_id = :userId2 " +
                    "AND f1.status = :status AND f2.status = :status";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId1", userId1)
                .addValue("userId2", userId2)
                .addValue("status", FriendshipStatus.CONFIRMED.name());
        return jdbc.query(sql, params, this::mapRowToUser);
    }

    private Set<Integer> loadFriends(int userId) {
        String sql = "SELECT friend_id FROM friendships WHERE user_id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return new HashSet<>(jdbc.query(sql, params, (rs, rowNum) -> rs.getInt("friend_id")));
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }
}