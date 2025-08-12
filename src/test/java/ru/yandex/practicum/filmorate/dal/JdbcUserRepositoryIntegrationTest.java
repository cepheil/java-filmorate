package ru.yandex.practicum.filmorate.dal;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({JdbcUserRepository.class, UserRowMapper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdbcUserRepositoryIntegrationTest {

    private final JdbcUserRepository userRepository;

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userRepository.create(user);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull().isPositive();
        assertThat(createdUser.getEmail()).isEqualTo("test@example.com");
        assertThat(createdUser.getLogin()).isEqualTo("testlogin");
        assertThat(createdUser.getName()).isEqualTo("Test User");
        assertThat(createdUser.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void testFindAllUsers() {
        // Создаем пользователей перед тестом
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userRepository.create(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1991, 2, 2));
        userRepository.create(user2);

        Collection<User> users = userRepository.findAll();

        assertThat(users).isNotEmpty();
        assertThat(users).extracting(User::getEmail)
                .contains("user1@example.com", "user2@example.com");
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setEmail("original@example.com");
        user.setLogin("original");
        user.setName("Original Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User createdUser = userRepository.create(user);

        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setEmail("updated@example.com");
        updatedUser.setLogin("updated");
        updatedUser.setName("Updated Name");
        updatedUser.setBirthday(LocalDate.of(1995, 5, 5));

        User result = userRepository.update(updatedUser);

        assertThat(result.getId()).isEqualTo(createdUser.getId());
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        assertThat(result.getLogin()).isEqualTo("updated");
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getBirthday()).isEqualTo(LocalDate.of(1995, 5, 5));
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User createdUser = userRepository.create(user);

        boolean deleted = userRepository.delete(createdUser.getId());

        assertThat(deleted).isTrue();

        Optional<User> foundUser = userRepository.findById(createdUser.getId());
        assertThat(foundUser).isEmpty();
    }

    @Test
    public void testDeleteNonExistentUser() {
        boolean deleted = userRepository.delete(999L);
        assertThat(deleted).isFalse();
    }

    @Test
    public void testFindNonExistentUser() {
        Optional<User> foundUser = userRepository.findById(999L);
        assertThat(foundUser).isEmpty();
    }
}