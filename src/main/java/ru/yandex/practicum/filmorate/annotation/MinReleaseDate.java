package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validator.MinReleaseDateValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация {@code MinReleaseDate} используется для валидации даты релиза фильма.
 * Она гарантирует, что дата не может быть раньше установленной минимальной даты (28 декабря 1895 года).
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinReleaseDateValidator.class)
public @interface MinReleaseDate {
    /**
     * Возврат сообщения об ошибке, если дата релиза меньше допустимой.
     * @return сообщение об ошибке
     */
    String message() default "Дата релиза не может быть раньше 28 декабря 1895 года";
    /**
     * Группы валидации, к которым относится эта аннотация.
     * @return массив групп валидации
     */
    Class<?>[] groups() default {};
    /**
     * Дополнительные данные (payload), связанные с ограничением.
     * @return массив объектов Payload
     */
    Class<? extends Payload>[] payload() default {};
}
