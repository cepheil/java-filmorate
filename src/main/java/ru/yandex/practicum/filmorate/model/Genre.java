package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Genre {
    private Long id;
    @NotBlank(message = "name не может быть пустым")
    private String name;

    public Genre(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
