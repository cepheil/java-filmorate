package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Qualifier("jdbc")
public class JdbcLikeRepository implements LikeRepository {
    private final JdbcTemplate jdbc;


    @Override
    public int addLike(Long filmId, Long userId) {
        String sqlAdd = "MERGE INTO likes (film_id, user_id) KEY(film_id, user_id) VALUES (?, ?)";
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

    @Override
    public void addLikesBatch(Long filmId, Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, userIds.stream()
                        .toList()
                        .get(i));
            }

            @Override
            public int getBatchSize() {
                return userIds.size();
            }
        });
    }

    @Override
    public void loadLikesForFilms(Map<Long, Film> filmMap) {
        if (filmMap.isEmpty()) {
            return;
        }

        String sqlLikes = """
                    SELECT film_id, user_id
                    FROM likes
                    WHERE film_id IN (%s)
                """.formatted(filmMap.keySet()
                .stream()
                .map(id -> "?")
                .collect(Collectors.joining(",")));

        jdbc.query(sqlLikes, rs -> {
            // для каждой строки находим фильм в мапе по id.
            Film film = filmMap.get(rs.getLong("film_id"));
            if (film.getLikes() == null) {
                film.setLikes(new HashSet<>());
            }
            // в каждой строе извлекаем "user_id" и добавляем в лист лайков фильма
            film.getLikes().add(rs.getLong("user_id"));
        }, filmMap.keySet().toArray());
    }


}
