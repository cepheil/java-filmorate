package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTest {
    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Фильм: Пустое имя → ошибка валидации")
    public void shouldFailIfNameIsBlank() {
        Film film = new Film();
        film.setName(" ");
        film.setDescription("Movie without name");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(100L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }


    @Test
    @DisplayName("Фильм:  Описание >200 символов → ошибка валидации")
    public void shouldFailIfDescriptionTooLong() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("A".repeat(201)); // создаем описание = 201 символ
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(100L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }


    @Test
    @DisplayName("Фильм:  Отрицательная продолжительность → ошибка валидации")
    public void shouldFailIfDurationIsNegative() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-10L);  // отрицательная продолжительность

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }


    @Test
    @DisplayName("Фильм: Продолжительность =0 → ошибка валидации")
    public void shouldFailIfDurationIsZero() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0L); // "нулевая" продолжительность

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }


    @Test
    @DisplayName("Фильм: корректные данные → нет ошибок")
    public void shouldPassWithValidData() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(100L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());  // нет ошибок - корректные данные.
    }


    @Test
    @DisplayName("Фильм: Дата релиза = Null → ошибка валидации")
    public void shouldFailIfReleaseDateIsNull() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Valid description");
        film.setReleaseDate(null);  // дата релиза не указана
        film.setDuration(100L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));
    }


    @Test
    @DisplayName("Фильм: Дата релиза до 28.12.1895 → ошибка валидации")
    public void shouldThrowExceptionIfReleaseDateBefore1895() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(100L);

        FilmController controller = new FilmController();

        //подтверждаем ошибку сравнения даты
        ValidationException thrown = assertThrows(ValidationException.class, () -> controller.createFilm(film));
        //подтверждаем описание ошибки сравнения даты
        assertEquals("дата релиза — не раньше 28 декабря 1895 года", thrown.getMessage());
    }

}
