package ru.yandex.practicum.filmorate.dal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JdbcFriendshipRepository.class, UserRowMapper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JdbcFriendshipRepositoryIntegrationTest {

    @Autowired
    private JdbcFriendshipRepository friendshipRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        // Очищаем и переинициализируем тестовые данные
        jdbcTemplate.update("DELETE FROM friendships");
        jdbcTemplate.update("DELETE FROM users");

        // Сбрасываем sequence
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");

        // Добавляем тестовых пользователей
        jdbcTemplate.update(
                "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "user1@example.com", "user1", "User One", LocalDate.of(1990, 1, 1)
        );
        jdbcTemplate.update(
                "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "user2@example.com", "user2", "User Two", LocalDate.of(1991, 2, 2)
        );
        jdbcTemplate.update(
                "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "user3@example.com", "user3", "User Three", LocalDate.of(1992, 3, 3)
        );

        jdbcTemplate.update(
                "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "user4@example.com", "user4", "User Four", LocalDate.of(1993, 4, 4)
        );

        // Добавляем тестовые дружеские связи
        jdbcTemplate.update(
                "INSERT INTO friendships (user_id, friend_id, confirmed) VALUES (?, ?, ?)",
                1L, 3L, true
        );
        jdbcTemplate.update(
                "INSERT INTO friendships (user_id, friend_id, confirmed) VALUES (?, ?, ?)",
                2L, 3L, true
        );
    }

    @Test
    public void testHasFriendship() {
        // Проверяем существующие дружеские связи
        assertThat(friendshipRepository.hasFriendship(1L, 3L)).isTrue();
        assertThat(friendshipRepository.hasFriendship(2L, 3L)).isTrue();

        // Проверяем отсутствие дружбы
        assertThat(friendshipRepository.hasFriendship(1L, 2L)).isFalse();
        assertThat(friendshipRepository.hasFriendship(3L, 1L)).isFalse(); // Дружба однонаправленная
    }

    @Test
    public void testAddAndRemoveFriend() {
        Long userId = 1L;
        Long friendId = 2L;

        // Проверяем, что изначально дружбы нет
        assertThat(friendshipRepository.hasFriendship(userId, friendId)).isFalse();

        // Добавляем дружбу (неподтвержденную)
        friendshipRepository.addFriend(userId, friendId);
        assertThat(friendshipRepository.hasFriendship(userId, friendId)).isTrue();
        assertThat(friendshipRepository.hasFriendship(friendId, userId)).isFalse(); // Однонаправленная

        // Удаляем дружбу
        friendshipRepository.removeFriend(userId, friendId);
        assertThat(friendshipRepository.hasFriendship(userId, friendId)).isFalse();
    }

    @Test
    public void testGetFriends() {
        // создаём пользователей
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "u1@example.com", "u1", "User1", LocalDate.of(1990, 1, 1));
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "u2@example.com", "u2", "User2", LocalDate.of(1991, 2, 2));
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "u3@example.com", "u3", "User3", LocalDate.of(1992, 3, 3));

        // создаём дружбу 3 ↔ 1 и 3 ↔ 2
        jdbcTemplate.update("INSERT INTO friendships (user_id, friend_id, confirmed) VALUES (3, 1, true)");
        jdbcTemplate.update("INSERT INTO friendships (user_id, friend_id, confirmed) VALUES (3, 2, true)");

        Collection<User> friendsOf3 = friendshipRepository.getFriends(3L);
        assertThat(friendsOf3)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    public void testGetCommonFriends() {
        jdbcTemplate.update("DELETE FROM friendships");
        // Добавляем общих друзей для теста
        friendshipRepository.addFriend(1L, 4L); // Создадим нового пользователя 4
        friendshipRepository.addFriend(2L, 4L);

        // Проверяем общих друзей между 1 и 2
        Collection<User> commonFriends = friendshipRepository.getCommonFriends(1L, 2L);
        assertThat(commonFriends)
                .hasSize(1)
                .extracting(User::getId)
                .containsExactly(4L);
    }


}