package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validator.MinReleaseDateValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация {@code MinReleaseDate} применяется к полю, представляющему дату релиза фильма.
 * Используется для проверки того, что указанная дата не раньше минимально допустимой — 28 декабря 1895 года.
 * Аннотация поддерживает интеграцию с Jakarta Bean Validation и работает через связанный валидатор
 * {@link ru.yandex.practicum.filmorate.validator.MinReleaseDateValidator}.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinReleaseDateValidator.class)
public @interface MinReleaseDate {
    String message() default "Дата релиза не может быть раньше 28 декабря 1895 года";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
