package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public final class ReleaseDateValidator implements ConstraintValidator<
        ReleaseDateValid, LocalDate> {
    private static final LocalDate CINEMA_BIRTH =
        LocalDate.of(
            1895,
            12,
            28
        );

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
