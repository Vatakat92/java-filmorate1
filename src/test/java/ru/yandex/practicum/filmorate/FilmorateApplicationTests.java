package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class FilmorateApplicationTests {

	@Test
	void shouldLoadContext() {
	}

	@Test
	void shouldStartApplicationSuccessfully() {
	}

	@Test
	void shouldRunMainMethodWithoutExceptions() {
		assertDoesNotThrow(() -> FilmorateApplication.main(new String[]{}));
	}
}
