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

    @GetMapping
    public List<Review> getAllReviews(@RequestParam(defaultValue = "10") int count) {
        return reviewService.getAllReviews(count);
    }

    @GetMapping("/film/{filmId}")
    public List<Review> getReviewsByFilmId(@PathVariable Long filmId,
                                           @RequestParam(defaultValue = "10") int count) {
        return reviewService.getReviewsByFilmId(filmId, count);
    }

    @PutMapping("/like/{reviewId}/{userId}")
    public void addLike(@PathVariable Long reviewId,
                        @PathVariable Long userId) {
        reviewService.addLike(reviewId, userId);
    }

    @PutMapping("/dislike/{reviewId}/{userId}")
    public void addDislike(@PathVariable Long reviewId,
                           @PathVariable Long userId) {
        reviewService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/like/{reviewId}/{userId}")
    public void removeLike(@PathVariable Long reviewId,
                           @PathVariable Long userId) {
        reviewService.removeLike(reviewId, userId);
    }

    @DeleteMapping("/dislike/{reviewId}/{userId}")
    public void removeDislike(@PathVariable Long reviewId,
                              @PathVariable Long userId) {
        reviewService.removeDislike(reviewId, userId);
    }
}
