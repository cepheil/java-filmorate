package ru.yandex.practicum.filmorate.storage.friend;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
@Qualifier("friendRepository")
@RequiredArgsConstructor
public class JdbcFriendRepository implements FriendRepository {

    private final JdbcTemplate jdbc;
    private final UserRowMapper userRowMapper;

    private static final String ADD_FRIEND_QUERY = """
            INSERT INTO friends (user_id, friend_id, confirmed)
            VALUES (?, ?, TRUE)
            """;

    private static final String CONFIRM_FRIENDSHIP_QUERY = """
            UPDATE friends
            SET confirmed = TRUE
            WHERE user_id = ? AND friend_id = ?
            """;

    private static final String REMOVE_FRIEND_QUERY = """
            DELETE FROM friends
            WHERE (user_id = ? AND friend_id = ?)
            """;

    private static final String GET_FRIENDS_QUERY = """
            SELECT u.user_id, u.email, u.login, u.name, u.birthday
            FROM users u
            JOIN friends f ON u.user_id = f.friend_id
            WHERE f.user_id = ?
            """;

    private static final String GET_COMMON_FRIENDS_QUERY = """
            SELECT u.user_id, u.email, u.login, u.name, u.birthday
            FROM users u
            WHERE u.user_id IN (
                SELECT f1.friend_id
                FROM friends f1
                JOIN friends f2 ON f1.friend_id = f2.friend_id
                WHERE f1.user_id = ? AND f2.user_id = ?
            """;

    private static final String CHECK_FRIENDSHIP_QUERY = """
            SELECT COUNT(*) > 0 AS has_friendship
            FROM friends
            WHERE user_id = ? AND friend_id = ? AND confirmed = TRUE
            """;

    private static final String CHECK_FRIEND_REQUEST_QUERY = """
            SELECT COUNT(*) > 0 AS has_request
            FROM friends
            WHERE user_id = ? AND friend_id = ? AND confirmed = FALSE
            """;
    @Override
    public void addFriend(Long userId, Long friendId) {
        Boolean hasReverseRequest = jdbc.queryForObject(
                CHECK_FRIEND_REQUEST_QUERY, Boolean.class, friendId, userId);

        if (hasReverseRequest != null && hasReverseRequest) {
            jdbc.update(CONFIRM_FRIENDSHIP_QUERY, userId, friendId);
            jdbc.update(CONFIRM_FRIENDSHIP_QUERY, friendId, userId);
        } else {
            jdbc.update(ADD_FRIEND_QUERY, userId, friendId);
        }
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        jdbc.update(REMOVE_FRIEND_QUERY, userId, friendId, friendId, userId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        return jdbc.query(GET_FRIENDS_QUERY, userRowMapper, userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId1, Long userId2) {
        return jdbc.query(GET_COMMON_FRIENDS_QUERY, userRowMapper, userId1, userId2);
    }

    @Override
    public boolean hasFriendship(Long userId, Long friendId) {
        Boolean result = jdbc.queryForObject(CHECK_FRIENDSHIP_QUERY, Boolean.class, userId,friendId);
        return result != null ? result : false;
    }
}
