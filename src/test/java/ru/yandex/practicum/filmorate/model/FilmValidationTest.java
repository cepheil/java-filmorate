package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки валидации модели {@link Film}.
 * Содержит unit-тесты, проверяющие корректность аннотаций валидации,
 * таких, как @NotBlank, @Size, @PastOrPresent и @Positive.
 */
public class FilmValidationTest {

    private Validator validator;

    /**
     * Инициализация валидатора перед каждым тестом.
     * Создание нового экземпляра {@link Validator} на основе фабрики JSR 380.
     */
    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Проверка, что название фильма не может быть пустым.
     * Аннотация @NotBlank требует обязательного наличия значения.
     */
    @Test
    public void testFilmNameCannotBeEmpty() {
        Film film = new Film();
        film.setName("");
        var violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    /**
     * Проверка, что описание фильма не может превышать 200 символов.
     * Аннотация @Size ограничивает максимальную длину строки.
     */
    @Test
    public void testFilmDescriptionWithMoreThan200Size() {
        Film film = new Film();
        film.setDescription("d".repeat(201));
        var violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    /**
     * Проверка, что дата релиза не может быть раньше 28.12.1895.
     * Аннотация @MinReleaseDate обеспечивает это ограничение.
     */
    @Test
    public void testReleaseDateBeforeCinemaBirthday() {
        Film film = new Film();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        var violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    /**
     * Проверка, что продолжительность фильма должна быть положительной.
     * Аннотация @Positive гарантирует, что значение больше нуля.
     */
    @Test
    public void testDurationMustBePositive() {
        Film film = new Film();
        film.setDuration(0);
        var violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }
}
