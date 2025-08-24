package ru.yandex.practicum.filmorate.storage.reviewLikes;

import ru.yandex.practicum.filmorate.model.ReviewLikes;

import java.util.Optional;

public interface ReviewLikesRepository {

    void addReviewLike(Long reviewId, Long userId, Boolean isLike);

    void deleteReviewLike(Long reviewId, Long userId);

    Optional<ReviewLikes> getReviewLikes(Long reviewId, Long userId);
}
