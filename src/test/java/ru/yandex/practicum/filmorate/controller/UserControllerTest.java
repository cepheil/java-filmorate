package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для проверки работы контроллера {@link UserController}.
 *
 * <p>Тесты покрывают основные методы контроллера:</p>
 * <ul>
 *     <li>Получение списка всех пользователей</li>
 *     <li>Создание пользователя</li>
 *     <li>Обновление данных пользователя</li>
 *     <li>Добавление и удаление друзей</li>
 *     <li>Получение списка друзей</li>
 *     <li>Поиск общих друзей между двумя пользователями</li>
 * </ul>
 *
 * <p>Для тестирования используется мок ({@link Mock}) объекта {@link UserService},
 * а тестируемый класс ({@link UserController}) внедряется через {@link InjectMocks}.</p>
 */
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Проверяет, что метод {@link UserController#findAllUsers()} возвращает список всех пользователей.
     *
     * <p>Ожидается, что вызов делегируется в {@link UserService#findAllUsers()}</p>
     */
    @Test
    public void testFindAllUsers() {
        User user1 = new User();
        User user2 = new User();
        Collection<User> users = Arrays.asList(user1, user2);

        when(userService.findAllUsers()).thenReturn(users);

        Collection<User> result = userController.findAllUsers();

        assertEquals(2, result.size());
        verify(userService, times(1)).findAllUsers();
    }

    /**
     * Проверяет, что метод {@link UserController#getFriends(Long)} возвращает список друзей указанного пользователя.
     *
     * <p>Ожидается, что вызов делегируется в {@link UserService#getFriends(Long)}</p>
     */
    @Test
    public void testGetFriends() {
        Long friendId = (Long) 1L;
        User friend1 = new User();
        User friend2 = new User();
        Collection<User> friends = Arrays.asList(friend1, friend2);

        when(userService.getFriends(friendId)).thenReturn(friends);

        Collection<User> result = userController.getFriends(friendId);

        assertEquals(2, result.size());
        verify(userService, times(1)).getFriends(friendId);
    }

    /**
     * Проверяет, что метод {@link UserController#getCommonFriends(Long, Long)} возвращает список общих друзей между двумя пользователями.
     *
     * <p>Ожидается, что вызов делегируется в {@link UserService#getCommonFriends(Long, Long)}</p>
     */
    @Test
    public void testGetCommonFriends() {
        Long friendId = (Long) 1L;
        Long userId = (Long) 2L;
        User commonFriend = new User();
        Collection<User> commonFriends = Arrays.asList(commonFriend);

        when(userService.getCommonFriends(friendId, userId)).thenReturn(commonFriends);

        Collection<User> result = userController.getCommonFriends(friendId, userId);

        assertEquals(1, result.size());
        verify(userService, times(1)).getCommonFriends(friendId, userId);
    }

    /**
     * Проверяет, что метод {@link UserController#createUser(User)} корректно создаёт нового пользователя.
     *
     * <p>Ожидается, что вызов делегируется в {@link UserService#createUser(User)}</p>
     */
    @Test
    public void testCreateUser() {
        User user = new User();

        when(userService.createUser(user)).thenReturn(user);

        User result = userController.createUser(user);

        assertEquals(user, result);
        verify(userService, times(1)).createUser(user);
    }

    /**
     * Проверяет, что метод {@link UserController#updateUser(User)} корректно обновляет данные существующего пользователя.
     *
     * <p>Ожидается, что вызов делегируется в {@link UserService#updateUser(User)}</p>
     */
    @Test
    public void testUpdateUser() {
        User user = new User();

        when(userService.updateUser(user)).thenReturn(user);

        User result = userController.updateUser(user);

        assertEquals(user, result);
        verify(userService, times(1)).updateUser(user);
    }

    /**
     * Проверяет, что метод {@link UserController#addFriend(Long, Long)} добавляет друга указанному пользователю.
     *
     * <p>Ожидается, что вызов делегируется в {@link UserService#addFriend(Long, Long)}</p>
     */
    @Test
    public void testAddFriend() {
        Long userId = (Long) 1L;
        Long friendId = (Long) 2L;

        userController.addFriend(userId, friendId);

        verify(userService, times(1)).addFriend(userId, friendId);
    }

    /**
     * Проверяет, что метод {@link UserController#removeFriend(Long, Long)} удаляет друга у указанного пользователя.
     *
     * <p>Ожидается, что вызов делегируется в {@link UserService#removeFriend(Long, Long)}</p>
     */
    @Test
    public void testRemoveFriend() {
        Long userId = (Long) 1L;
        Long friendId = (Long) 2L;

        userController.removeFriend(userId, friendId);

        verify(userService, times(1)).removeFriend(userId, friendId);
    }
}

