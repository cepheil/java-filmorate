package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.MinReleaseDate;

import java.time.LocalDate;

/**
 * Реализация валидатора для проверки минимальной даты релиза фильма.
 *
 * <p>Этот валидатор используется вместе с аннотацией {@link MinReleaseDate} и гарантирует,
 * что дата релиза фильма не раньше 28 декабря 1895 года — официальной даты рождения кинематографа.</p>
 *
 * <p>Если значение равно {@code null}, оно считается валидным (позволяет полю быть опциональным).</p>
 *
 * @see MinReleaseDate
 * @see ConstraintValidator
 */
public class MinReleaseDateValidator implements ConstraintValidator<MinReleaseDate, LocalDate> {
    /**
     * Константа, представляющая минимально допустимую дату релиза фильма.
     *
     * <p>Дата соответствует 28 декабря 1895 года — дню первого публичного показа фильма братьями Люмьер.</p>
     */
    public static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    /**
     * Проверяет, является ли указанная дата релиза допустимой.
     *
     * @param date дата релиза фильма ({@link LocalDate}), может быть {@code null}
     * @param context контекст валидации
     * @return {@code true}, если дата равна {@code null} или не ранее 28.12.1895, иначе {@code false}
     */
    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        return date == null || !date.isBefore(CINEMA_BIRTHDAY);
    }
}
