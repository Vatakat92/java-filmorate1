package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void shouldCreateUser() {
        // given
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setEmail("test@example.com");
        savedUser.setLogin("testuser");
        savedUser.setName("Test User");
        savedUser.setBirthday(LocalDate.of(1990, 1, 1));

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        User result = userService.createUser(user);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsEmpty() {
        // given
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName(""); // пустое имя
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setEmail("test@example.com");
        savedUser.setLogin("testuser");
        savedUser.setName("testuser"); // должно стать логином
        savedUser.setBirthday(LocalDate.of(1990, 1, 1));

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        User result = userService.createUser(user);

        // then
        assertThat(result.getName()).isEqualTo("testuser");
    }

    @Test
    void shouldGetUserById() {
        // given
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setLogin("testuser");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // when
        User result = userService.getUserById(1);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    void shouldGetAllUsers() {
        // given
        User user1 = new User();
        user1.setId(1);
        User user2 = new User();
        user2.setId(2);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // when
        List<User> result = userService.getAllUsers();

        // then
        assertThat(result).hasSize(2);
    }
}