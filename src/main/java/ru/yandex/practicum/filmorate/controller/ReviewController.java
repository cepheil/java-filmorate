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

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(required = false) Long filmId,
                                   @RequestParam(defaultValue = "10") int count) {
        if (filmId != null) {
            return reviewService.getReviewsByFilmId(filmId, count);
        } else {
            return reviewService.getAllReviews(count);
        }
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public void addLike(@PathVariable Long reviewId,
                        @PathVariable Long userId) {
        reviewService.addLike(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public void addDislike(@PathVariable Long reviewId,
                           @PathVariable Long userId) {
        reviewService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public void removeLike(@PathVariable Long reviewId,
                           @PathVariable Long userId) {
        reviewService.deleteLike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void removeDislike(@PathVariable Long reviewId,
                              @PathVariable Long userId) {
        reviewService.deleteDislike(reviewId, userId);
    }
}
