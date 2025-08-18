package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserCreateRequest;
import ru.yandex.practicum.filmorate.dto.UserResponse;
import ru.yandex.practicum.filmorate.dto.UserUpdateRequest;
import ru.yandex.practicum.filmorate.mapper.UserDtoMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody final UserCreateRequest request) {
        User createdUser = userService.createUser(userDtoMapper.toDomain(request));
        UserResponse response = userDtoMapper.toResponse(createdUser);
        log.info("Пользователь добавлен: {}", response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody final UserUpdateRequest request) {
        User updatedUser = userService.updateUser(userDtoMapper.toDomain(request));
        UserResponse response = userDtoMapper.toResponse(updatedUser);
        log.info("Пользователь обновлён: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        log.debug("Запрос всех пользователей");
        return userDtoMapper.toResponseList(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable int id) {
        log.debug("Запрос пользователя id={}", id);
        return userDtoMapper.toResponse(userService.getUserById(id));
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Добавление в друзья: userId={}, friendId={}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Удаление из друзей: userId={}, friendId={}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserResponse> getFriends(@PathVariable int id) {
        log.debug("Запрос друзей пользователя id={}", id);
        return userDtoMapper.toResponseList(userService.getFriends(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserResponse> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.debug("Запрос общих друзей: userId={}, otherId={}", id, otherId);
        return userDtoMapper.toResponseList(userService.getCommonFriends(id, otherId));
    }
}
