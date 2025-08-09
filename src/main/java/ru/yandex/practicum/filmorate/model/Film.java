package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    private Set<Long> likes = new HashSet<>();
    private Long id;

    @NotBlank(message = "Name не может быть пустым")
    @Size(max=200)
    private String name;

    @Size(max = 500, message = "Описание не должно превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза должна быть указана")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность не может быть пустой")
    @Positive(message = "Продолжительность должна быть положительной")
    private Long duration;

    //@NotEmpty(message = "Фильм должен относиться хотя бы к одному жанру")
    private List<Genre> genres = new ArrayList<>();

    @NotNull(message = "фильм должен иметь рейтинг")
    private RatingMpa rating;
}
