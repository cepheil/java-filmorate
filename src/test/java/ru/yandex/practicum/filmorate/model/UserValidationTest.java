package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Тестовый класс для проверки валидации модели {@link User}.
 * Содержит unit-тесты, проверяющие корректность аннотаций валидации,
 * таких, как @NotNull, @Email, @Pattern и @PastOrPresent.
 */
public class UserValidationTest {

    private Validator validator;

    /**
     * Инициализация валидатора перед каждым тестом.
     * Создание нового экземпляра {@link Validator} на основе фабрики JSR 380.
     */
    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Проверка, что email не может быть null.
     * Аннотация @NotNull требует обязательного заполнения поля.
     */
    @Test
    public void testEmailIsRequired() {
        User user = new User();
        user.setEmail(null);
        var violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    /**
     * Проверка, что логин не может содержать пробелы.
     * Аннотация @Pattern запрещает использование пробельных символов.
     */
    @Test
    public void testLoginCannotContainSpaces() {
        User user = new User();
        user.setLogin("l o g i n");
        var violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    /**
     * Проверка, что дата рождения не может быть в будущем.
     * Аннотация @PastOrPresent требует, чтобы дата была не позже текущей.
     */
    @Test
    public void testBirthdayCannotBeInFuture() {
        User user = new User();
        user.setBirthday(LocalDate.now().plusDays(1));
        var violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }
}
