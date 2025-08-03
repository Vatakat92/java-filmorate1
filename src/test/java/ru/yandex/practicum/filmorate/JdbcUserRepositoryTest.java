package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.JdbcUserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({JdbcUserRepository.class})
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("JdbcUserRepository")
class JdbcUserRepositoryTest {

    private final JdbcUserRepository userRepository;

    @Test
    @DisplayName("should find user by id")
    public void should_return_user_when_find_by_id() {
        // when
        Optional<User> userOptional = userRepository.findById(1001);

        assertThat(userOptional)
                .isPresent()
                .get()
                .satisfies(user -> {
                    assertThat(user.getId()).isEqualTo(1001);
                    assertThat(user.getEmail()).isEqualTo("test1@example.com");
                    assertThat(user.getLogin()).isEqualTo("testuser1");
                    assertThat(user.getName()).isEqualTo("Test User 1");
                });
    }

    @Test
    @DisplayName("should save user")
    public void should_save_user() {
        User newUser = new User();
        newUser.setEmail("unique@test.com");
        newUser.setLogin("uniqueuser");
        newUser.setName("New User");
        newUser.setBirthday(LocalDate.of(1995, 6, 15));

        User savedUser = userRepository.save(newUser);

        assertThat(savedUser)
                .isNotNull()
                .satisfies(user -> {
                    assertThat(user.getId()).isPositive();
                    assertThat(user.getEmail()).isEqualTo("unique@test.com");
                    assertThat(user.getLogin()).isEqualTo("uniqueuser");
                });
    }

    @Test
    @DisplayName("should update user")
    public void should_update_user() {
        // используем существующего пользователя
        User existingUser = userRepository.findById(1001).orElseThrow();
        existingUser.setName("Updated Name");
        existingUser.setEmail("updated@test.com");

        User updatedUser = userRepository.update(existingUser);

        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@test.com");
    }

    @Test
    @DisplayName("should return all users")
    public void should_return_all_users() {
        List<User> allUsers = userRepository.findAll();

        // проверяем предзаполненных пользователей
        assertThat(allUsers)
                .hasSizeGreaterThanOrEqualTo(4)
                .extracting(User::getLogin)
                .contains("testuser1", "testuser2", "testuser3", "admin");
    }

    @Test
    @DisplayName("should add and get friends")
    public void should_add_and_get_friends() {
        // используем существующих пользователей
        int user1Id = 1001;
        int user2Id = 1002;

        userRepository.addFriend(user1Id, user2Id);

        List<User> user1Friends = userRepository.getFriends(user1Id);
        assertThat(user1Friends)
                .isNotEmpty()
                .extracting(User::getId)
                .contains(user2Id);
    }

    @Test
    @DisplayName("should support one-sided friendship")
    public void should_support_one_sided_friendship() {
        // используем существующих пользователей
        int user1Id = 1001;
        int user4Id = 1004;

        // добавляем одностороннюю дружбу
        userRepository.addFriend(user1Id, user4Id);

        List<User> user1Friends = userRepository.getFriends(user1Id);
        List<User> user4Friends = userRepository.getFriends(user4Id);

        assertThat(user1Friends).extracting(User::getId).contains(user4Id);
        assertThat(user4Friends).extracting(User::getId).doesNotContain(user1Id);
    }

    @Test
    @DisplayName("should remove friends")
    public void should_remove_friends() {
        // используем существующих пользователей с дружбой
        int user1Id = 1001;
        int user2Id = 1002;

        userRepository.removeFriend(user1Id, user2Id);

        List<User> user1Friends = userRepository.getFriends(user1Id);
        assertThat(user1Friends)
                .extracting(User::getId)
                .doesNotContain(user2Id);
    }

    @Test
    @DisplayName("should find common friends")
    public void should_find_common_friends() {
        int user1Id = 1001;
        int user2Id = 1002;

        List<User> commonFriends = userRepository.getCommonFriends(user1Id, user2Id);

        assertThat(commonFriends)
                .isNotEmpty()
                .extracting(User::getId)
                .contains(1003);
    }
}
