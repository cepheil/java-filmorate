package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    Review createReview(Review review);

    Review updateReview(Review review);

    boolean deleteReview(Long reviewId);

    Optional<Review> getReviewById(Long reviewId);

    List<Review> getReviewsByFilmId(Long filmId, int count);

    void addLike(Long reviewId, Long userId);

    void addDislike(Long reviewId, Long userId);
}
