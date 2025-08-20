package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ValidationService validationService;

    public Review createReview(Review review) {
        log.info("Попытка создания отзыва для фильма ID: {}, пользователем ID: {}",
                review.getFilmId(), review.getUserId());
        validationService.validateReview(review);
        return reviewRepository.createReview(review);
    }

    public Review updateReview(Review review) {
        log.info("Попытка обновить отзыв {}", review.getReviewId());
        validationService.validateReview(review);
        validationService.validateReviewExists(review.getReviewId());
        return reviewRepository.updateReview(review);
    }

    public boolean deleteReview(Long reviewId) {
        log.info("Попытка удаления отзыва с ID: {}", reviewId);
        validationService.validateReviewExists(reviewId);
        return reviewRepository.deleteReview(reviewId);
    }

    public Review getReviewById(Long reviewId) {
        log.info("Попытка получения отзыва по ID: {}", reviewId);
        validationService.validateReviewExists(reviewId);
        return reviewRepository.getReviewById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с ID " + reviewId + " не найден"));
    }

    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        log.info("Попытка получения {} отзыва для фильма с ID: {}", count, filmId);
        validationService.validateFilmExists(filmId);
        return reviewRepository.getReviewsByFilmId(filmId, count);
    }

    public List<Review> getAllReviews(int count) {
        log.info("Попытка получения всех отзывов (лимит: {})", count);
        return reviewRepository.getAllReviews(count);
    }

    public void addLike(Long reviewId, Long userId) {
        log.info("Пользователь ID: {} Ставит лайк отзыву ID: {}", userId, reviewId);
        validationService.validateReviewExists(reviewId);
        validationService.validateUserExists(userId);
        reviewRepository.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        log.info("Пользователь ID: {} Ставит дизлайк отзыву ID: {}", userId, reviewId);
        validationService.validateReviewExists(reviewId);
        validationService.validateUserExists(userId);
        reviewRepository.addDislike(reviewId, userId);
    }

    public void removeLike(Long reviewId, Long userId) {
        log.info("Пользователь ID: {} удаляет лайк с отзыва ID: {}", userId, reviewId);
        validationService.validateReviewExists(reviewId);
        validationService.validateUserExists(userId);
        reviewRepository.removeLike(reviewId, userId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        log.info("Пользователь ID: {} удаляет дизлайк с отзыва ID: {}", userId, reviewId);
        validationService.validateReviewExists(reviewId);
        validationService.validateUserExists(userId);
        reviewRepository.removeDislike(reviewId, userId);
    }
}
