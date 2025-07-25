package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private int id;
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email должен быть корректным")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелы")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();
}
