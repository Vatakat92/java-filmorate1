package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class FilmorateApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void applicationStartsSuccessfully() {
	}

	@Test
	void mainMethodRunsWithoutExceptions() {
		assertDoesNotThrow(() -> FilmorateApplication.main(new String[]{}));
	}
}
