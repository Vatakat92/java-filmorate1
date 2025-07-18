package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerValidationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void createUser_whenEmailIsEmpty_thenBadRequest() throws Exception {
        String userJson = "{" +
                "\"email\":\"\"," +
                "\"login\":\"login\"," +
                "\"name\":\"name\"," +
                "\"birthday\":\"2000-01-01\"}";
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_whenEmailWithoutAt_thenBadRequest() throws Exception {
        String userJson = "{" +
                "\"email\":\"mail.ru\"," +
                "\"login\":\"login\"," +
                "\"name\":\"name\"," +
                "\"birthday\":\"2000-01-01\"}";
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_whenLoginIsEmpty_thenBadRequest() throws Exception {
        String userJson = "{" +
                "\"email\":\"mail@mail.ru\"," +
                "\"login\":\"\"," +
                "\"name\":\"name\"," +
                "\"birthday\":\"2000-01-01\"}";
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_whenLoginWithSpace_thenBadRequest() throws Exception {
        String userJson = "{" +
                "\"email\":\"mail@mail.ru\"," +
                "\"login\":\"lo gin\"," +
                "\"name\":\"name\"," +
                "\"birthday\":\"2000-01-01\"}";
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_whenNameIsEmpty_thenNameSetAsLogin() throws Exception {
        String userJson = "{" +
                "\"email\":\"mail@mail.ru\"," +
                "\"login\":\"login\"," +
                "\"name\":\"\"," +
                "\"birthday\":\"2000-01-01\"}";
        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("login");
        user.setName("login");
        user.setBirthday(java.time.LocalDate.of(2000, 1, 1));
        when(userService.createUser(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(user);
        String responseJson = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        assertTrue(responseJson.contains("\"name\":\"login\""));
    }

    @Test
    void createUser_whenBirthdayInFuture_thenBadRequest() throws Exception {
        String userJson = "{" +
                "\"email\":\"mail@mail.ru\"," +
                "\"login\":\"login\"," +
                "\"name\":\"name\"," +
                "\"birthday\":\"2050-01-01\"}";
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }
}
