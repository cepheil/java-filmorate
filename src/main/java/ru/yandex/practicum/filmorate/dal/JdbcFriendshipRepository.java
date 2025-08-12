package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;


@Repository
@RequiredArgsConstructor
@Qualifier("jdbc")
public class JdbcFriendshipRepository implements FriendshipRepository {
    private final UserRowMapper mapper;
    private final JdbcTemplate jdbc;


    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = "MERGE INTO friendships (user_id, friend_id, confirmed)" +
                " KEY(user_id, friend_id) VALUES (?, ?, false)";
        jdbc.update(sql, userId, friendId);
    }


    @Override
    public void removeFriend(Long userId, Long friendId) {
        String deleteSql = """
                DELETE FROM friendships
                WHERE user_id = ? AND friend_id = ?
                """;
        jdbc.update(deleteSql, userId, friendId);
    }


    @Override
    public Collection<User> getFriends(Long userId) {
        String getFriendsSql = """
                SELECT u.*
                FROM friendships AS f
                JOIN users AS u ON f.friend_id = u.id
                WHERE f.user_id = ?
                """;
        return jdbc.query(getFriendsSql, mapper, userId);
    }


    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        String getCommonSql = """
                SELECT u.*
                FROM friendships AS f1
                JOIN friendships AS f2 ON f1.friend_id = f2.friend_id
                JOIN users AS u ON  f1.friend_id = u.id
                WHERE f1.user_id = ? AND f2.user_id = ?
                """;
        return jdbc.query(getCommonSql, mapper, userId, otherUserId);
    }


    @Override
    public void confirmFriendship(Long userId, Long friendId) {
        String updateSql = """
                UPDATE friendships SET confirmed = true
                WHERE user_id = ? AND friend_id = ?
                """;
        jdbc.update(updateSql, userId, friendId);
    }

    @Override
    public boolean hasFriendship(Long userId, Long friendId) {
        String sql = "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, userId, friendId);
        return count != null && count > 0;
    }


}
