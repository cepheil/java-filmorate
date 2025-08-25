package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendRepository;
import ru.yandex.practicum.filmorate.storage.friend.JdbcFriendRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        JdbcUserRepository.class,
        UserRowMapper.class,
        JdbcFriendRepository.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JdbcUserRepositoryIntegrationTest {

    @Autowired
    private JdbcUserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAllUsers();
    }

    @Test
    public void testCreateUser() {
        User user = createUniqueUser("testuser");
        User createdUser = userRepository.createUser(user);
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull().isPositive();
        assertThat(createdUser.getEmail()).isEqualTo("testuser@example.com");
        assertThat(createdUser.getLogin()).isEqualTo("testuser");
        assertThat(createdUser.getName()).isEqualTo("Test User");
        assertThat(createdUser.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void testFindAllUsers() {
        User user1 = createUniqueUser("user1");
        User user2 = createUniqueUser("user2");
        User user3 = createUniqueUser("user3");
        userRepository.createUser(user1);
        userRepository.createUser(user2);
        userRepository.createUser(user3);
        List<User> users = userRepository.findAllUsers();
        assertThat(users).hasSize(3);
        assertThat(users).extracting(User::getEmail)
                .containsExactlyInAnyOrder(
                        user1.getEmail(),
                        user2.getEmail(),
                        user3.getEmail()
                );
    }

    @Test
    public void testUpdateUser() {
        User original = createUniqueUser("original");
        User createdUser = userRepository.createUser(original);
        User updated = User.builder()
                .id(createdUser.getId())
                .email("updated@example.com")
                .login("updatedLogin")
                .name("Updated Name")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();
        User result = userRepository.updateUser(updated);
        assertThat(result.getId()).isEqualTo(createdUser.getId());
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        assertThat(result.getLogin()).isEqualTo("updatedLogin");
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getBirthday()).isEqualTo(LocalDate.of(1995, 5, 5));
    }

    @Test
    public void testDeleteUser() {
        User user = createUniqueUser("deletetest");
        User createdUser = userRepository.createUser(user);
        boolean deleted = userRepository.deleteUser(createdUser.getId());
        assertThat(deleted).isTrue();
        Optional<User> foundUser = userRepository.getUserById(createdUser.getId());
        assertThat(foundUser).isEmpty();
    }

    @Test
    public void testDeleteNonExistentUser() {
        boolean deleted = userRepository.deleteUser(999L);
        assertThat(deleted).isFalse();
    }

    @Test
    public void testFindNonExistentUser() {
        Optional<User> foundUser = userRepository.getUserById(999L);
        assertThat(foundUser).isEmpty();
    }

    @Test
    public void testDuplicateEmail() {
        User user1 = createUniqueUser("user1");
        User user2 = createUniqueUser("user2");
        user2.setEmail(user1.getEmail());
        userRepository.createUser(user1);
        try {
            userRepository.createUser(user2);
            throw new AssertionError("Expected exception was not thrown");
        } catch (Exception e) {
            // Expected behavior
        }
        List<User> users = userRepository.findAllUsers();
        assertThat(users).hasSize(1);
    }

    @Test
    public void testFriendOperations() {
        User user1 = createUniqueUser("user1");
        User user2 = createUniqueUser("user2");
        User user3 = createUniqueUser("user3");
        userRepository.createUser(user1);
        userRepository.createUser(user2);
        userRepository.createUser(user3);
        friendRepository.addFriend(user1.getId(), user2.getId());
        friendRepository.addFriend(user1.getId(), user3.getId());
        List<User> friends = friendRepository.getFriends(user1.getId());
        assertThat(friends).hasSize(2);
        assertThat(friends).extracting(User::getId)
                .containsExactlyInAnyOrder(user2.getId(), user3.getId());
        friendRepository.removeFriend(user1.getId(), user2.getId());
        friends = friendRepository.getFriends(user1.getId());
        assertThat(friends).hasSize(1);
        assertThat(friends).extracting(User::getId)
                .containsExactly(user3.getId());
    }

    @Test
    public void testCommonFriends() {
        User user1 = createUniqueUser("user1");
        User user2 = createUniqueUser("user2");
        User commonFriend = createUniqueUser("common");
        userRepository.createUser(user1);
        userRepository.createUser(user2);
        userRepository.createUser(commonFriend);
        friendRepository.addFriend(user1.getId(), commonFriend.getId());
        friendRepository.addFriend(user2.getId(), commonFriend.getId());
        List<User> commonFriends = friendRepository.getCommonFriends(user1.getId(), user2.getId());
        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends.get(0).getId()).isEqualTo(commonFriend.getId());
    }

    @Test
    public void testDeleteUserWithFriends() {
        User user1 = createUniqueUser("user1");
        User user2 = createUniqueUser("user2");
        userRepository.createUser(user1);
        userRepository.createUser(user2);
        friendRepository.addFriend(user1.getId(), user2.getId());
        userRepository.deleteUser(user1.getId());
        Optional<User> foundUser = userRepository.getUserById(user1.getId());
        assertThat(foundUser).isEmpty();
        List<User> friends = friendRepository.getFriends(user2.getId());
        assertThat(friends).isEmpty();
    }

    private User createUniqueUser(String username) {
        return User.builder()
                .email(username + "@example.com")
                .login(username)
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
    }
}

