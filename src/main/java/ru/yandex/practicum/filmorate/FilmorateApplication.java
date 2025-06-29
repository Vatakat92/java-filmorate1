package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения Filmorate.
 * Запускает Spring Boot приложение.
 */
@SpringBootApplication
public class FilmorateApplication {
    /**
     * Точка входа в приложение.
     * @param args аргументы командной строки
     */
    public static void main(final String[] args) {
        SpringApplication.run(FilmorateApplication.class, args);
    }
}
