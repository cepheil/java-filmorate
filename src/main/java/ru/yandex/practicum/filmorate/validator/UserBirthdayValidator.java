package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.ValidBirthday;

import java.time.LocalDate;

/**
 * Реализация валидатора для проверки даты рождения пользователя.
 *
 * <p>Этот валидатор используется вместе с аннотацией {@link ValidBirthday} и гарантирует,
 * что дата рождения не находится в будущем.</p>
 *
 * <p>Если значение равно {@code null}, оно считается валидным (позволяет полю быть опциональным).</p>
 *
 * @see ValidBirthday
 * @see ConstraintValidator
 */
public class UserBirthdayValidator implements ConstraintValidator<ValidBirthday, LocalDate> {

    /**
     * Проверяет, является ли указанная дата рождения допустимой.
     *
     * @param value   дата рождения пользователя ({@link LocalDate}), может быть {@code null}
     * @param context контекст валидации
     * @return {@code true}, если дата равна {@code null} или не позже текущей даты, иначе {@code false}
     */
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isAfter(LocalDate.now());
    }
}
