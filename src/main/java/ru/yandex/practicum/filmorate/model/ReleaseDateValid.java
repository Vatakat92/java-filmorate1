package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Аннотация для валидации даты релиза фильма.
 * Проверяет, что дата не раньше 28 декабря 1895 года.
 */
@Documented
@Constraint(validatedBy = ReleaseDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReleaseDateValid {
    /**
     * Сообщение об ошибке по умолчанию.
     * @return текст сообщения
     */
    String message() default
        "Дата релиза не может быть раньше 28 декабря 1895 года";
    /**
     * Группы валидации.
     * @return группы
     */
    Class<?>[] groups() default {};
    /**
     * Дополнительная информация о нагрузке.
     * @return payload
     */
    Class<? extends Payload>[] payload() default {};
}
