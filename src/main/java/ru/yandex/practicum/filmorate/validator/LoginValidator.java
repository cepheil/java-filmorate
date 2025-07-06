package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.ValidLogin;

import java.util.regex.Pattern;

/**
 * Реализация валидатора для проверки формата логина пользователя.
 *
 * <p>Этот валидатор используется вместе с аннотацией {@link ValidLogin} и гарантирует,
 * что логин соответствует заданному формату: только буквы латинского алфавита и цифры, без пробелов.</p>
 *
 * <p>Пример допустимого логина: "user123"</p>
 * <p>Примеры недопустимых значений: "user name", "user@domain", null</p>
 *
 * @see ValidLogin
 * @see ConstraintValidator
 */
public class LoginValidator implements ConstraintValidator<ValidLogin, String> {
    /**
     * Регулярное выражение для проверки формата логина.
     *
     * <p>Логин должен содержать только символы a-z, A-Z и 0-9, минимум один символ.</p>
     */
    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    /**
     * Проверяет, соответствует ли значение заданному формату логина.
     *
     * @param value   значение поля, которое необходимо проверить
     * @param context контекст валидации
     * @return {@code true}, если значение не равно {@code null} и соответствует формату, иначе {@code false}
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && LOGIN_PATTERN.matcher(value).matches();
    }
}
