package ru.yandex.practicum.filmorate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.update(user);
    }

    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id = " + id + " не найден"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пользователь не может добавить самого себя в друзья");
        }
        checkUserExists(userId);
        checkUserExists(friendId);
        userRepository.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        checkUserExists(userId);
        checkUserExists(friendId);
        userRepository.removeFriend(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        checkUserExists(userId);
        return userRepository.getFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        checkUserExists(userId);
        checkUserExists(otherId);
        return userRepository.getCommonFriends(userId, otherId);
    }

    private void checkUserExists(int userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id = " + userId + " не найден");
        }
    }
}
