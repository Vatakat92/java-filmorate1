package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

/**
 * Валидатор для аннотации
 * {@link ReleaseDateValid}.
 * Проверяет,
 * что дата релиза не раньше
 * 28 декабря 1895 года.
 */
public final class ReleaseDateValidator implements ConstraintValidator<
        ReleaseDateValid, LocalDate> {
    /**
     * Дата рождения кинематографа — 28 декабря 1895 года.
     */
    private static final LocalDate CINEMA_BIRTH =
        LocalDate.of(
            1895,
            12,
            28
        );

    /**
     * Проверяет, что дата не раньше 28 декабря 1895 года.
     * @param value дата для проверки
     * @param context контекст валидации
     * @return true, если дата валидна или null, иначе false
     */
    @Override
    public boolean isValid(final LocalDate value,
                           final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return !value.isBefore(
            CINEMA_BIRTH
        );
    }
}
