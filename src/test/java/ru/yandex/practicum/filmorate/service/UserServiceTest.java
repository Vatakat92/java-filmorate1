package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserService userService;
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = mock(UserStorage.class);
        userService = new UserService(userStorage);
    }

    @Test
    void shouldAddFriendToBothUsersIfBothExist() {
        User user1 = new User();
        user1.setId(1);
        user1.setLogin("user1");
        user1.setEmail("user1@mail.ru");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        user1.setFriends(new java.util.HashSet<>());

        User user2 = new User();
        user2.setId(2);
        user2.setLogin("user2");
        user2.setEmail("user2@mail.ru");
        user2.setBirthday(LocalDate.of(2000, 2, 2));
        user2.setFriends(new java.util.HashSet<>());

        when(userStorage.containsUser(1)).thenReturn(true);
        when(userStorage.containsUser(2)).thenReturn(true);
        when(userStorage.getAllUsers()).thenReturn(java.util.List.of(user1, user2));

        userService.addFriend(1, 2);

        assertTrue(user1.getFriends().contains(2));
        assertTrue(user2.getFriends().contains(1));
    }
}
