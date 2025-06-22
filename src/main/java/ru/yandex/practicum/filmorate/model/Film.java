package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "максимальная длина описания — 200 символов")
    private String description;
    @NotNull(message = "Дата релиза не может быть пустой")
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    private LocalDate releaseDate;
    @Positive(message = "продолжительность фильма должна быть положительным числом")
    private Duration duration;
}
