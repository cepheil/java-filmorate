package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLikes;
import ru.yandex.practicum.filmorate.storage.review.ReviewRepository;
import ru.yandex.practicum.filmorate.storage.reviewLikes.ReviewLikesRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ValidationService validationService;
    private final ReviewLikesRepository reviewLikesRepository;
    private final EventService eventService;

    public Review createReview(Review review) {
        log.info("Попытка создания отзыва для фильма ID: {}, пользователем ID: {}",
                review.getFilmId(), review.getUserId());
        validationService.validateReview(review);
        Review newReview = reviewRepository.createReview(review);
        eventService.addEvent(review.getUserId(), newReview.getReviewId(), "REVIEW", "ADD");
        return newReview;
    }

    public Review updateReview(Review review) {
        log.info("Попытка обновить отзыв {}", review.getReviewId());
        validationService.validateReview(review);
        validationService.validateReviewExists(review.getReviewId());
        eventService.addEvent(review.getUserId(), review.getReviewId(), "REVIEW", "UPDATE");
        return reviewRepository.updateReview(review);
    }

    public void deleteReview(Long reviewId) {
        log.info("Попытка удаления отзыва с ID: {}", reviewId);
        validationService.validateReviewExists(reviewId);
        Review review = reviewRepository.getReviewById(reviewId).get();
        eventService.addEvent(review.getUserId(), reviewId, "REVIEW", "REMOVE");
        reviewRepository.deleteReview(reviewId);
    }

    public Review getReviewById(Long reviewId) {
        log.info("Попытка получения отзыва по ID: {}", reviewId);
        validationService.validateReviewExists(reviewId);
        return reviewRepository.getReviewById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с ID " + reviewId + " не найден"));
    }

    public List<Review> getAllReviews(int count) {
        log.info("Попытка получения {} отзывов", count);
        if (count <= 0) {
            throw new ValidationException("Число отзывов должно быть положительным");
        }
        return reviewRepository.getAllReviews(count);
    }

    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        log.info("Попытка получения {} отзыва для фильма с ID: {}", count, filmId);
        validationService.validateFilmExists(filmId);
        return reviewRepository.getReviewsByFilmId(filmId, count);
    }

    public void addLike(Long reviewId, Long userId) {
        log.info("Попытка добавления лайка: пользователь {} ставит лайк отзыву {}", userId, reviewId);
        validationService.validateReviewExists(reviewId);
        Optional<ReviewLikes> rLikes = reviewLikesRepository.getReviewLikes(reviewId, userId);
        if (rLikes.isPresent()) {
            if (rLikes.get().getIsLike().equals(Boolean.TRUE)) {
                log.trace("Лайк отзыву {} от пользователя {} уже существует", reviewId, userId);
                return;
            } else {
                log.trace("На отзыве {} стоит дизлайк от пользователя {}. Ставим лайк", reviewId, userId);
                reviewLikesRepository.deleteReviewLike(reviewId, userId);
                reviewRepository.addLike(reviewId, userId);
                reviewRepository.updateUseful(reviewId);
            }
        }
        reviewLikesRepository.addReviewLike(reviewId, userId, Boolean.TRUE);
        log.info("Пользователь {} лайкнул отзыв {}", userId, reviewId);
        reviewRepository.addLike(reviewId, userId);
        reviewRepository.updateUseful(reviewId);
    }

    public void addDislike(Long reviewId, Long userId) {
        log.info("Попытка добавления дизлайка: пользователь {} ставит дизлайк отзыву {}", userId, reviewId);
        validationService.validateReviewExists(reviewId);
        Optional<ReviewLikes> rLikes = reviewLikesRepository.getReviewLikes(reviewId, userId);
        if (rLikes.isPresent()) {
            if (rLikes.get().getIsLike().equals(Boolean.FALSE)) {
                log.trace("Дизайк отзыву {} от пользователя {} уже существует", reviewId, userId);
                return;
            } else {
                log.trace("На отзыве {} стоит лайк от пользователя {}. Ставим дизлайк", reviewId, userId);
                reviewLikesRepository.deleteReviewLike(reviewId, userId);
                reviewRepository.addDislike(reviewId, userId);
                reviewRepository.updateUseful(reviewId);
            }
        }
        reviewLikesRepository.addReviewLike(reviewId, userId, Boolean.FALSE);
        log.info("Пользователь {} дизлайкнул отзыв {}", userId, reviewId);
        reviewRepository.addDislike(reviewId, userId);
        reviewRepository.updateUseful(reviewId);
    }

    public void deleteLike(Long reviewId, Long userId) {
        log.info("Попытка удаления лайка: пользователь {} убирает лайк отзыву {}", userId, reviewId);
        validationService.validateReviewExists(reviewId);
        Optional<ReviewLikes> rLikes = reviewLikesRepository.getReviewLikes(reviewId, userId);
        if (rLikes.isPresent()) {
            reviewLikesRepository.deleteReviewLike(reviewId, userId);
            if (rLikes.get().getIsLike().equals(Boolean.TRUE)) {
                log.trace("Удаляем лайк отзыву {} от пользователя {}", reviewId, userId);
                reviewRepository.addDislike(reviewId, userId);
                reviewRepository.updateUseful(reviewId);
            }
        }
    }

    public void deleteDislike(Long reviewId, Long userId) {
        log.info("Попытка удаления дизлайка: пользователь {} убирает дизлайк отзыву {}", userId, reviewId);
        validationService.validateReviewExists(reviewId);
        Optional<ReviewLikes> rLikes = reviewLikesRepository.getReviewLikes(reviewId, userId);
        if (rLikes.isPresent()) {
            reviewLikesRepository.deleteReviewLike(reviewId, userId);
            if (rLikes.get().getIsLike().equals(Boolean.FALSE)) {
                log.trace("Удаляем дизлайк отзыву {} от пользователя {}", reviewId, userId);
                reviewRepository.addLike(reviewId, userId);
                reviewRepository.updateUseful(reviewId);
            }
        }
    }
}
