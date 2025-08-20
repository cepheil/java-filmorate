package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.base.BaseNamedParameterRepository;

import java.util.*;

@Repository
@Qualifier("reviewRepository")
public class JdbcReviewRepository extends BaseNamedParameterRepository<Review> implements ReviewRepository {
    private static final String INSERT_REVIEW_QUERY = """
            INSERT INTO reviews (content, is_positive, user_id, film_id, rating)
            VALUES (:content, :isPositive, :userId, :filmId, rating)
            """;

    private static final String UPDATE_REVIEW_QUERY = """
            UPDATE reviews
            SET content = :content, is_positive = :isPositive, user_id = :userId, film_id = :filmId
            WHERE review_id = :reviewId;
            """;

    private static final String DELETE_REVIEW_QUERY = "DELETE FROM reviews WHERE review_id = :reviewId";

    private static final String FIND_REVIEW_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = :reviewId";

    private static final String FIND_REVIEWS_BY_FILM_ID_QUERY = """
            SELECT * FROM reviews
            WHERE film_id = :filmId
            ORDER BY rating DESC
            LIMIT :count
            """;

    private static final String FIND_ALL_REVIEWS_QUERY = """
            SELECT * FROM reviews
            ORDER BY rating DESC
            LIMIT :count
            """;

    private static final String ADD_LIKE_QUERY = """
            INSERT INTO review_likes (review_id, user_id, is_like)
            VALUES (:reviewId, :userId, TRUE)
            ON DUPLICATE KEY UPDATE is_like = TRUE
            """;

    private static final String ADD_DISLIKE_QUERY = """
            INSERT INTO review_likes (review_id, user_id, is_like)
            VALUES (:reviewId, :userId, FALSE)
            ON DUPLICATE KEY UPDATE is_like = FALSE
            """;

    private static final String REMOVE_LIKE_QUERY = """
            DELETE FROM review_likes
            WHERE review_id = :reviewId AND user_id = :userId AND is_like = TRUE
            """;

    private static final String REMOVE_DISLIKE_QUERY = """
            DELETE FROM review_likes 
            WHERE review_id = :reviewId AND user_id = :userId AND is_like = FALSE
            """;

    private static final String UPDATE_RATING_QUERY = """
            UPDATE reviews
            SET rating = rating + :delta
            WHERE review_id = :reviewId
            """;

    public JdbcReviewRepository(NamedParameterJdbcOperations jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review createReview(Review review) {
        Map<String, Object> params = new HashMap<>();
        params.put("content", review.getContent());
        params.put("isPositive", review.getIsPositive());
        params.put("userId", review.getUserId());
        params.put("filmId", review.getFilmId());
        params.put("rating", 0);

        long id = insert(INSERT_REVIEW_QUERY, params);
        review.setReviewId(id);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        Map<String, Object> params = new HashMap<>();
        params.put("content", review.getContent());
        params.put("isPositive", review.getIsPositive());
        params.put("userId", review.getUserId());
        params.put("filmId", review.getFilmId());
        params.put("reviewId", review.getReviewId());

        update(UPDATE_REVIEW_QUERY, params);
        return review;
    }

    @Override
    public boolean deleteReview(Long reviewId) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", reviewId);
        return update(DELETE_REVIEW_QUERY, params);
    }

    @Override
    public Optional<Review> getReviewById(Long reviewId) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", reviewId);
        return findOne(FIND_REVIEW_BY_ID_QUERY, params);
    }

    @Override
    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);
        params.put("count", count);
        return findMany(FIND_REVIEWS_BY_FILM_ID_QUERY, params);
    }

    @Override
    public List<Review> getAllReviews(int count) {
        Map<String, Object> params = new HashMap<>();
        params.put("count", count);
        return findMany(FIND_ALL_REVIEWS_QUERY, params);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", reviewId);
        params.put("userId", userId);
        removeDislike(reviewId, userId);
        jdbc.update(ADD_LIKE_QUERY, params);
        updateRating(reviewId, 1);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", reviewId);
        params.put("userId", userId);
        removeLike(reviewId, userId);
        jdbc.update(ADD_DISLIKE_QUERY, params);
        updateRating(reviewId, -1);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", reviewId);
        params.put("userId", userId);
        jdbc.update(REMOVE_LIKE_QUERY, params);
        updateRating(reviewId, -1);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", reviewId);
        params.put("userId", userId);
        jdbc.update(REMOVE_DISLIKE_QUERY, params);
        updateRating(reviewId, 1);
    }

    private void updateRating(Long reviewId, int delta) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", reviewId);
        params.put("delta", delta);
        jdbc.update(UPDATE_RATING_QUERY, params);
    }
}
