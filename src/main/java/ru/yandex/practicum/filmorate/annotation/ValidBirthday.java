package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validator.UserBirthdayValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserBirthdayValidator.class)
public @interface ValidBirthday {
    String message() default "Дата рождения должна быть в прошлом.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
