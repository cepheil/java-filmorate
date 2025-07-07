package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.MinReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс {@code Film} представляет модель фильма, которая содержит информацию о фильме, включая:
 * название, описание, дату релиза, продолжительность и список пользователей, поставивших лайк.
 *
 * <p>Поля класса:</p>
 * <ul>
 *     <li>{@link #id} — уникальный идентификатор фильма</li>
 *     <li>{@link #name} — название фильма (не может быть пустым)</li>
 *     <li>{@link #description} — краткое описание фильма (не более 200 символов)</li>
 *     <li>{@link #releaseDate} — дата выхода фильма (не может быть пустой, не должна быть в будущем и не раньше 28 декабря 1895 года)</li>
 *     <li>{@link #duration} — длительность фильма в минутах (должна быть положительным числом)</li>
 *     <li>{@link #likes} — коллекция ID пользователей, которым понравился фильм</li>
 * </ul>
 *
 * <p>Валидация:</p>
 * <ul>
 *     <li>{@link NotBlank} — поле {@code name} обязательно для заполнения</li>
 *     <li>{@link Size} — ограничение на длину поля {@code description}</li>
 *     <li>{@link PastOrPresent} — поле {@code releaseDate} не может быть в будущем</li>
 *     <li>{@link MinReleaseDate} — гарантирует, что дата релиза не раньше 28 декабря 1895 года</li>
 *     <li>{@link Positive} — поле {@code duration} должно быть больше нуля</li>
 * </ul>
 */
@Data
public class Film {
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "максимальная длина описания — 200 символов")
    private String description;
    @NotNull(message = "Дата релиза не может быть пустой")
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    @MinReleaseDate
    private LocalDate releaseDate;
    @Positive(message = "продолжительность фильма должна быть положительным числом, в минутах")
    private Integer duration;
    private Set<Long> likes = new HashSet<>();
}
