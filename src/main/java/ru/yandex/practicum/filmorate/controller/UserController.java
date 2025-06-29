package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер для управления пользователями.
 * Позволяет добавлять, обновлять и получать список пользователей.
 */
@Slf4j
@RestController
@RequestMapping("/users")
public final class UserController {
    /**
     * Список всех пользователей в памяти приложения.
     */
    private final List<User> users = new ArrayList<>();
    private int userIdCounter = 1;

    /**
     * Создаёт нового пользователя.
     * @param user добавляемый пользователь
     * @return добавленный пользователь
     */
    @PostMapping
    public User createUser(@Valid @RequestBody final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(userIdCounter++);
        users.add(user);
        log.info("Пользователь добавлен: {}", user);
        return user;
    }

    /**
     * Обновляет существующего пользователя.
     * @param user обновляемый пользователь
     * @return обновлённый пользователь
     */
    @PutMapping
    public User updateUser(@Valid @RequestBody final User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                users.set(i, user);
                log.info("Пользователь обновлён: {}", user);
                return user;
            }
        }
        throw new IllegalArgumentException("Пользователь с id=" + user.getId() + " не найден");
    }

    /**
     * Возвращает список всех пользователей.
     * @return список пользователей
     */
    @GetMapping
    public List<User> getAllUsers() {
        return users;
    }
}
