package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
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
