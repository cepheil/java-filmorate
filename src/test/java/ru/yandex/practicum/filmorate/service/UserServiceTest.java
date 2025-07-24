package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@mail.com");
        user1.setLogin("login1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));


        user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@mail.com");
        user2.setLogin("login2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1991, 2, 2));

        user1.setFriends(new HashMap<>());
        user2.setFriends(new HashMap<>());

        when(userStorage.getUserById(1L)).thenReturn(user1);
        when(userStorage.getUserById(2L)).thenReturn(user2);
    }

    @Test
    @DisplayName("Отправка запроса на дружбу → PENDING/REQUEST_RECEIVED")
    void testAddFriendRequest() {
        userService.addFriend(1L, 2L);

        assertEquals(FriendshipStatus.PENDING, user1.getFriends().get(2L));
        assertEquals(FriendshipStatus.REQUEST_RECEIVED, user2.getFriends().get(1L));
    }

    @Test
    @DisplayName("Повторная отправка запроса не меняет статус")
    void testAddFriendRequestTwice() {
        userService.addFriend(1L, 2L);
        userService.addFriend(1L, 2L); // второй запрос

        assertEquals(FriendshipStatus.PENDING, user1.getFriends().get(2L));
        assertEquals(FriendshipStatus.REQUEST_RECEIVED, user2.getFriends().get(1L));
    }

    @Test
    @DisplayName("Подтверждение дружбы → оба CONFIRMED")
    void testConfirmFriend() {
        // Сначала создаем запрос
        userService.addFriend(1L, 2L);

        // Подтверждаем со стороны user2
        userService.confirmFriend(2L, 1L);

        assertEquals(FriendshipStatus.CONFIRMED, user1.getFriends().get(2L));
        assertEquals(FriendshipStatus.CONFIRMED, user2.getFriends().get(1L));
    }

    @Test
    @DisplayName("Подтверждение без запроса → ConditionsNotMetException")
    void testConfirmFriendWithoutRequest() {
        assertThrows(ConditionsNotMetException.class, () -> userService.confirmFriend(2L, 1L));
    }

    @Test
    @DisplayName("Удаление дружбы → оба удаляются из друзей")
    void testRemoveFriend() {
        userService.addFriend(1L, 2L);
        userService.confirmFriend(2L, 1L);

        userService.removeFriend(1L, 2L);

        assertFalse(user1.getFriends().containsKey(2L));
        assertFalse(user2.getFriends().containsKey(1L));
    }

    @Test
    @DisplayName("Нельзя добавить самого себя в друзья → ValidationException")
    void testAddSelfAsFriend() {
        assertThrows(RuntimeException.class, () -> userService.addFriend(1L, 1L));
    }

    @Test
    @DisplayName("Пользователь не найден → NotFoundException")
    void testUserNotFound() {
        when(userStorage.getUserById(99L)).thenReturn(null);
        assertThrows(NotFoundException.class, () -> userService.addFriend(1L, 99L));
    }
}