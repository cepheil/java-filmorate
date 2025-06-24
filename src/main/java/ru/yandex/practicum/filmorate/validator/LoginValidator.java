package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.ValidLogin;

import java.util.regex.Pattern;

/**
 * Валидатор для проверки формата логина пользователя.
 * Логин должен содержать только буквы латинского алфавита и цифры, без пробелов.
 */
public class LoginValidator implements ConstraintValidator<ValidLogin, String> {
    /**
     * Регулярное выражение, которому должен соответствовать логин:
     * только буквы a-zA-Z и цифры 0-9, минимум один символ.
     */
    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    /**
     * Проверяет, соответствует ли значение заданному формату логина.
     * @param value значение поля, которое проверяется
     * @param context контекст валидации
     * @return true, если значение соответствует регулярному выражению, иначе false
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && LOGIN_PATTERN.matcher(value).matches();
    }
}
