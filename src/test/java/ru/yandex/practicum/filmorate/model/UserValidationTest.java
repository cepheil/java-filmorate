package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserValidationTest {
    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    @DisplayName("Пользователь: Пустой email → ошибка валидации")
    public void shouldFailIfEmailIsBlank() {
        User user = new User();
        user.setEmail(" ");
        user.setLogin("Admin");
        user.setName("Bob");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email") &&
                        v.getMessage().contains("Email не может быть пустым")));
    }


    @Test
    @DisplayName("Пользователь: email = null → ошибка валидации")
    public void shouldFailIfEmailIsNull() {
        User user = new User();
        user.setEmail(null);
        user.setLogin("Admin");
        user.setName("Bob");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email") &&
                        v.getMessage().contains("Email не может быть пустым")));
    }


    @Test
    @DisplayName("Пользователь: email не соответствует формату → ошибка валидации")
    public void shouldFailIfEmailIsNotValid() {
        User user = new User();
        user.setEmail("admin.com");
        user.setLogin("Admin");
        user.setName("Bob");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email") &&
                        v.getMessage().contains("Некорректный формат email")));
    }


    @Test
    @DisplayName("Пользователь: login пустой → ошибка валидации")
    public void shouldFailIfLoginIsBlank() {
        User user = new User();
        user.setEmail("admin@mail.com");
        user.setLogin(" ");
        user.setName("BobLazar");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("login") &&
                        v.getMessage().contains("Логин не может быть пустым")));
    }


    @Test
    @DisplayName("Пользователь: login содержит пробелы → ошибка валидации")
    public void shouldFailIfLoginNotValid() {
        User user = new User();
        user.setEmail("admin@mail.com");
        user.setLogin("Bob Lazar");  // не должен содержать пробелы
        user.setName("BobLazar");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("login") &&
                        v.getMessage().contains("Логин не должен содержать пробелы")));
    }


    @Test
    @DisplayName("Пользователь: birthday = null → ошибка валидации")
    public void shouldFailIfBirthdayNull() {
        User user = new User();
        user.setEmail("admin@mail.com");
        user.setLogin("admin");
        user.setName("BobLazar");
        user.setBirthday(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("birthday") &&
                        v.getMessage().contains("Дата рождения не может быть пустой")));

    }


    @Test
    @DisplayName("Пользователь: birthday не может быть в будущем → ошибка валидации")
    public void testBirthdayCannotBeInFuture() {
        User user = new User();
        user.setEmail("admin@mail.com");
        user.setLogin("admin");
        user.setName("BobLazar");
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("birthday") &&
                        v.getMessage().contains("Дата рождения не может быть в будущем")));

    }
}
