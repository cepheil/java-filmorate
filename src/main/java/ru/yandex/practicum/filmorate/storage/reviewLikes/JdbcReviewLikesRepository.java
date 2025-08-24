package ru.yandex.practicum.filmorate.storage.reviewLikes;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.ReviewLikes;
import ru.yandex.practicum.filmorate.storage.base.BaseNamedParameterRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@Qualifier("reviewLikes")
public class JdbcReviewLikesRepository extends BaseNamedParameterRepository<ReviewLikes> implements ReviewLikesRepository {

    private static final String INSERT_REVIEW_LIKES_QUERY = """
            INSERT INTO review_likes (review_id, user_id, is_like)
            VALUES (:reviewId, :userId, :isLike)
            """;

    private static final String DELETE_REVIEW_LIKES_QUERY = """
            DELETE FROM review_likes
            WHERE review_id = :reviewId AND user_id = :userId
            """;

    private static final String FIND_REVIEW_LIKES_BY_ID_QUERY = """
            SELECT rl.*
            FROM review_likes rl
            WHERE review_id = :reviewId AND user_id = :userId
            """;

    public JdbcReviewLikesRepository(NamedParameterJdbcOperations jdbc, RowMapper<ReviewLikes> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void addReviewLike(Long reviewId, Long userId, Boolean isLike) {
        Map<String,Object> params = new HashMap<>();
        params.put("reviewId", reviewId);
        params.put("userId", userId);
        params.put("isLike", isLike);
        update(INSERT_REVIEW_LIKES_QUERY, params);
    }

    @Override
    public void deleteReviewLike(Long reviewId, Long userId) {
        Map<String,Object> params = new HashMap<>();
        params.put("reviewId", reviewId);
        params.put("userId", userId);
        update(DELETE_REVIEW_LIKES_QUERY, params);
    }

    @Override
    public Optional<ReviewLikes> getReviewLikes(Long reviewId, Long userId) {
        Map<String,Object> params = new HashMap<>();
        params.put("reviewId", reviewId);
        params.put("userId", userId);
        return findOne(FIND_REVIEW_LIKES_BY_ID_QUERY, params);
    }
}
