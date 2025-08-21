package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Long reviewId;
    @NotBlank(message = "Содержание отзыва не может быть пустым.")
    @Size(max = 1000, message = "Максимальная длина отзыва - 1000 символов.")
    private String content;
    @NotNull(message = "Тип отзыва обязателен.")
    Boolean isPositive;
    @NotNull(message = "Пользователь обязателен.")
    private Long userId;
    @NotNull(message = "Фильм обязателен.")
    private Long filmId;
    private Integer useful;
}
