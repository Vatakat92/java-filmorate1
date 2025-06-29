package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerValidationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnBadRequestIfEmailIsEmpty() throws Exception {
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
    void shouldReturnBadRequestIfEmailWithoutAt() throws Exception {
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
    void shouldReturnBadRequestIfLoginIsEmpty() throws Exception {
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
    void shouldReturnBadRequestIfLoginWithSpace() throws Exception {
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
    void shouldSetNameAsLoginIfNameIsEmpty() throws Exception {
        String userJson = "{" +
                "\"email\":\"mail@mail.ru\"," +
                "\"login\":\"login\"," +
                "\"name\":\"\"," +
                "\"birthday\":\"2000-01-01\"}";
        String responseJson = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();

        assertTrue(responseJson.contains("\"name\":\"login\""));
    }

    @Test
    void shouldReturnBadRequestIfBirthdayInFuture() throws Exception {
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
