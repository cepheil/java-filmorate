package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Класс {@code Genre} представляет модель жанра фильма.
 *
 * <p>Используется для классификации фильмов по категориям, таким как "Драма", "Комедия", "Боевик" и т.д.</p>
 *
 * <p>Поля класса:</p>
 * <ul>
 *     <li>{@link #id} — уникальный идентификатор жанра</li>
 *     <li>{@link #name} — название жанра (обязательное поле, не может быть пустым)</li>
 * </ul>
 *
 * <p>Валидация:</p>
 * <ul>
 *     <li>{@link NotBlank} — поле {@code name} обязательно для заполнения и не может состоять только из пробелов</li>
 * </ul>
 *
 * <p>Этот класс используется в составе модели {@link Film} для хранения списка жанров фильма.</p>
 */
@Data
public class Genre {
    private int id;
    @NotBlank(message = "Жанр не может быть пустым")
    private String name;
}
