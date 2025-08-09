package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Qualifier("jdbc")
public class JdbcLikeRepository implements LikeRepository {
    private final JdbcTemplate jdbc;


    @Override
    public int addLike(Long filmId, Long userId) {
        String sqlAdd = "INSERT INTO likes (film_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        return jdbc.update(sqlAdd, filmId, userId);
    }

    @Override
    public int removeLike(Long filmId, Long userId) {
        String sqlRemove = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        return jdbc.update(sqlRemove, filmId, userId);
    }

    @Override
    public int removeAllLikes(Long filmId) {
        String sql = "DELETE FROM likes WHERE film_id = ?";
        return jdbc.update(sql, filmId);
    }

}
