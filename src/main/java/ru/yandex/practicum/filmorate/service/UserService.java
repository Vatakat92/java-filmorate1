package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(int userId) {
        User user = getUserOrThrow(userId);
        List<User> friends = new ArrayList<>();
        for (Integer friendId : user.getFriends()) {
            if (userStorage.containsUser(friendId)) {
                friends.add(getUserOrThrow(friendId));
            }
        }
        return friends;
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = getUserOrThrow(userId);
        User other = getUserOrThrow(otherId);
        Set<Integer> commonIds = new HashSet<>(user.getFriends());
        commonIds.retainAll(other.getFriends());
        List<User> commonFriends = new ArrayList<>();
        for (Integer id : commonIds) {
            if (userStorage.containsUser(id)) {
                commonFriends.add(getUserOrThrow(id));
            }
        }
        return commonFriends;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (!userStorage.containsUser(user.getId())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Пользователь с id = " + user.getId() + " не найден");
        }
        return userStorage.updateUser(user);
    }

    public User getUserById(int id) {
        return getUserOrThrow(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    private User getUserOrThrow(int id) {
        return userStorage.getAllUsers().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Пользователь с id = " + id + " не найден"));
    }
}
