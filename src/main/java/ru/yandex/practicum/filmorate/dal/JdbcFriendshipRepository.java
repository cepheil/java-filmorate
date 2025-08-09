package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;


@Repository
@RequiredArgsConstructor
@Qualifier("jdbc")
public class JdbcFriendshipRepository implements FriendshipRepository {
    private final UserRowMapper mapper;
    private final JdbcTemplate jdbc;


    @Override
    public void addFriend(Long userId, Long friendId) {
        // Вставляем PENDING-запрос от user → friend, если его ещё нет
        String insertSql = """
                INSERT INTO friendships (user_id, friend_id, status)
                VALUES (?, ?, 'PENDING')
                ON CONFLICT (user_id, friend_id)
                """;

        String sql = "MERGE INTO friendships (user_id, friend_id, status)" +
                " KEY(user_id, friend_id) VALUES (?, ?, 'PENDING')";



        jdbc.update(sql, userId, friendId);  //insertSql

        // Вставляем REQUEST_RECEIVED запись от friend → user, если её ещё нет
        String insertReverse = """
                INSERT INTO friendships (user_id, friend_id, status)
                VALUES (?, ?, 'REQUEST_RECEIVED')
                ON CONFLICT (user_id, friend_id) DO NOTHING
                """;

        String sqlReverse = "MERGE INTO friendships (user_id, friend_id, status)" +
                " KEY(user_id, friend_id) VALUES (?, ?, 'REQUEST_RECEIVED')";

        jdbc.update(sqlReverse, friendId, userId);

        // Если встречный PENDING уже был — подтверждаем сразу
        String checkReverseSql = """
                SELECT status FROM friendships WHERE user_id = ? AND friend_id = ?
                """;

        List<String> statuses = jdbc.query(
                checkReverseSql,
                (rs, rowNum) -> rs.getString("status"),
                friendId, userId
        );

        if (!statuses.isEmpty() && "PENDING".equals(statuses.get(0))) {
            confirmFriendship(userId, friendId);
        }
    }

    private void confirmFriendship(Long userId, Long friendId) {
        String updateSql = """
                UPDATE friendships SET status = 'CONFIRMED'
                WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)
                """;
        jdbc.update(updateSql, userId, friendId, friendId, userId);
    }

    @Override
    public void confirmFriend(Long userId, Long friendId) {
        // Проверка: должен существовать входящий запрос REQUEST_RECEIVED
        String checkSql = """
                SELECT COUNT(*) FROM friendships
                WHERE user_id = ? AND friend_id = ? AND status = 'REQUEST_RECEIVED'
                """;
        Integer cnt = jdbc.queryForObject(checkSql, Integer.class, userId, friendId);

        if (cnt == null || cnt == 0) {
            throw new ConditionsNotMetException("Нет входящего запроса на дружбу от пользователя " + friendId);
        }

        confirmFriendship(userId,friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String deleteSql = """
                DELETE FROM friendships
                WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)
                """;
        jdbc.update(deleteSql, userId, friendId, friendId, userId);
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        String getFriendsSql = """
                SELECT u.*
                FROM friendships AS f
                JOIN users AS u ON f.friend_id = u.id
                WHERE f.user_id = ? AND  f.status = 'CONFIRMED'
                """;

        return jdbc.query(getFriendsSql, mapper, userId);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        String getCommonSql ="""
                SELECT u.*
                FROM friendships AS f1
                JOIN friendships AS f2 ON f1.friend_id = f2.friend_id
                JOIN users AS u ON  f1.friend_id = u.id
                WHERE f1.user_id = ? AND f2.user_id = ?
                AND f1.status = 'CONFIRMED' AND f2.status = 'CONFIRMED'
                """;

        return jdbc.query(getCommonSql, mapper, userId, otherUserId);
    }

    @Override
    public boolean existsByUserIdAndFriendId(Long userId, Long friendId) {
        String sql = "SELECT COUNT(*) FROM friendships " +
                "WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbc.queryForObject(
                sql,
                Integer.class,
                userId,
                friendId
        );
        return count != null && count > 0;
    }
}
