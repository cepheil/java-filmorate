package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ReviewLikes;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewLikesRowMapper implements RowMapper<ReviewLikes> {
    @Override
    public ReviewLikes mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return ReviewLikes.builder()
                .reviewId(resultSet.getLong("review_id"))
                .userId(resultSet.getLong("user_id"))
                .isLike(resultSet.getBoolean("is_like"))
                .build();
    }
}
