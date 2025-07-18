package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {
    private InMemoryUserStorage storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryUserStorage();
    }

    @Test
    void shouldAddUserAndAssignIdIfValidUser() {
        User user = new User();
        user.setLogin("login");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setName("");
        User created = storage.createUser(user);
        assertNotNull(created);
        assertEquals(1, created.getId());
        assertEquals("login", created.getName());
        assertTrue(storage.containsUser(1));
    }

    @Test
    void shouldUpdateUserIfExists() {
        User user = new User();
        user.setLogin("login");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User created = storage.createUser(user);
        created.setName("newName");
        storage.updateUser(created);
        List<User> all = storage.getAllUsers();
        assertEquals(1, all.size());
        assertEquals("newName", all.getFirst().getName());
    }

    @Test
    void getAllUsers_whenUsersExist_thenReturnAllUsers() {
        User user1 = new User();
        user1.setLogin("login1");
        user1.setEmail("mail1@mail.ru");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        storage.createUser(user1);
        User user2 = new User();
        user2.setLogin("login2");
        user2.setEmail("mail2@mail.ru");
        user2.setBirthday(LocalDate.of(2001, 2, 2));
        storage.createUser(user2);
        List<User> all = storage.getAllUsers();
        assertEquals(2, all.size());
    }

    @Test
    void containsUser_whenUserExists_thenReturnTrueElseFalse() {
        User user = new User();
        user.setLogin("login");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User created = storage.createUser(user);
        assertTrue(storage.containsUser(created.getId()));
        assertFalse(storage.containsUser(999));
    }
}
