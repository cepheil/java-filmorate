package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class Genre implements Comparable<Genre> {
    private Long id;
    @NotBlank(message = "Жанр не может быть пустым")
    private String name;

    @Override
    public int compareTo(Genre other) {
        return id.intValue() - other.id.intValue();
    }
}
