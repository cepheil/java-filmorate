package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * Создать новый отзыв.
     *
     * @param review объект отзыва для создания
     * @return созданный отзыв с присвоенным ID
     */
    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        log.info("Получен запрос на создание отзыва: {}", review);
        return reviewService.createReview(review);
    }

    /**
     * Обновить существующий отзыв.
     *
     * @param review объект отзыва с обновлёнными данными
     * @return обновлённый отзыв
     */
    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("Получен запрос на обновление отзыва с ID: {}", review.getReviewId());
        return reviewService.updateReview(review);
    }

    /**
     * Удалить отзыв по ID.
     *
     * @param id идентификатор отзыва для удаления
     */
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        log.info("Получен запрос на удаление отзыва с ID: {}", id);
        reviewService.deleteReview(id);
    }

    /**
     * Получить отзыв по ID.
     *
     * @param id идентификатор отзыва
     * @return отзыв с указанным ID
     * NotFoundException если отзыв не найден
     */
    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Long id) {
        log.info("Получен запрос на получение отзыва с ID: {}", id);
        return reviewService.getReviewById(id);
    }

    /**
     * Получить список отзывов.
     *
     * @param filmId (опционально) идентификатор фильма для фильтрации
     * @param count  количество отзывов для возврата (по умолчанию 10)
     * @return коллекция отзывов
     */
    @GetMapping
    public List<Review> getReviews(@RequestParam(required = false) Long filmId,
                                   @RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос на получение отзывов: filmId={}, count={}", filmId, count);
        if (filmId != null) {
            return reviewService.getReviewsByFilmId(filmId, count);
        } else {
            return reviewService.getAllReviews(count);
        }
    }

    /**
     * Добавить лайк отзыву.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя, который ставит лайк
     */
    @PutMapping("/{reviewId}/like/{userId}")
    public void addLike(@PathVariable Long reviewId,
                        @PathVariable Long userId) {
        log.info("Получен запрос на добавление лайка отзыву с ID {} от пользователя с ID {}", reviewId, userId);
        reviewService.addLike(reviewId, userId);
    }

    /**
     * Добавить дизлайк отзыву.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя, который ставит дизлайк
     */
    @PutMapping("/{reviewId}/dislike/{userId}")
    public void addDislike(@PathVariable Long reviewId,
                           @PathVariable Long userId) {
        log.info("Получен запрос на добавление дизлайка отзыву с ID {} от пользователя с ID {}", reviewId, userId);
        reviewService.addDislike(reviewId, userId);
    }

    /**
     * Удалить лайк отзыва.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя, который удаляет лайк
     */
    @DeleteMapping("/{reviewId}/like/{userId}")
    public void removeLike(@PathVariable Long reviewId,
                           @PathVariable Long userId) {
        log.info("Получен запрос на удаление лайка отзыва с ID {} от пользователя с ID {}", reviewId, userId);
        reviewService.deleteLike(reviewId, userId);
    }

    /**
     * Удалить дизлайк отзыва.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя, который удаляет дизлайк
     */
    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void removeDislike(@PathVariable Long reviewId,
                              @PathVariable Long userId) {
        log.info("Получен запрос на удаление дизлайка отзыва с ID {} от пользователя с ID {}", reviewId, userId);
        reviewService.deleteDislike(reviewId, userId);
    }
}
