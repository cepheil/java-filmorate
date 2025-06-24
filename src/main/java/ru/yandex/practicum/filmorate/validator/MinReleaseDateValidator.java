package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.MinReleaseDate;

import java.time.LocalDate;

/**
 * Валидатор для проверки даты релиза фильма.
 * Проверяет, что дата не раньше 28 декабря 1895 года.
 */
public class MinReleaseDateValidator implements ConstraintValidator<MinReleaseDate, LocalDate> {
    /**
     * Константа, представляющая минимально допустимую дату релиза фильма.
     */
    public static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    /**
     * Проверяет, является ли переданная дата корректной (не null и не раньше CINEMA_BIRTHDAY).
     * @param date дата для проверки
     * @param context контекст валидации
     * @return true, если дата равна null или не раньше 28.12.1895, иначе false
     */
    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        return date == null || !date.isBefore(CINEMA_BIRTHDAY);
    }
}
