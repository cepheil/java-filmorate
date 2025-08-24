package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
public class ReviewLikes {
    private Long reviewId;
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Long userId;
    private Boolean isLike;
}
