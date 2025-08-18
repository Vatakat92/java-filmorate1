package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.UserCreateRequest;
import ru.yandex.practicum.filmorate.dto.UserResponse;
import ru.yandex.practicum.filmorate.dto.UserUpdateRequest;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public class UserDtoMapper {

    public User toDomain(UserCreateRequest req) {
        User u = new User();
        u.setEmail(req.getEmail());
        u.setLogin(req.getLogin());
        u.setName(req.getName());
        u.setBirthday(req.getBirthday());
        return u;
    }

    public User toDomain(UserUpdateRequest req) {
        User u = new User();
        u.setId(req.getId());
        u.setEmail(req.getEmail());
        u.setLogin(req.getLogin());
        u.setName(req.getName());
        u.setBirthday(req.getBirthday());
        return u;
    }

    public UserResponse toResponse(User u) {
        UserResponse dto = new UserResponse();
        dto.setId(u.getId());
        dto.setEmail(u.getEmail());
        dto.setLogin(u.getLogin());
        dto.setName(u.getName());
        dto.setBirthday(u.getBirthday());
        return dto;
    }

    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream().map(this::toResponse).toList();
    }
}
