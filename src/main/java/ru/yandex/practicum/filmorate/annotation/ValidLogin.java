package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validator.LoginValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация {@code ValidLogin} применяется к полю, представляющему логин пользователя.
 * Используется для проверки того, что логин содержит только буквы и цифры и не включает пробелы.
 * Аннотация поддерживает интеграцию с Jakarta Bean Validation и работает через связанный валидатор
 * {@link ru.yandex.practicum.filmorate.validator.LoginValidator}.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LoginValidator.class)
public @interface ValidLogin {
    String message() default "Логин должен содержать только буквы и цифры и не может содержать пробелов.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
