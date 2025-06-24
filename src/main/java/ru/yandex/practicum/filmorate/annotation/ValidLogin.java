package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validator.LoginValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация {@code ValidLogin} используется для валидации логина.
 * Она гарантирует, что логин будет содержать только буквы и цифры и не может содержать пробелов.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LoginValidator.class)
public @interface ValidLogin {
    /**
     * Возврат сообщения об ошибке, логин не соответствует шаблону.
     * @return сообщение об ошибке
     */
    String message() default "Логин должен содержать только буквы и цифры и не может содержать пробелов.";
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
