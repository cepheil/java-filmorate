package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

/**
 * Контроллер {@code UserController} обрабатывает HTTP-запросы, связанные с пользователями.
 *
 * <p>Поддерживает следующие операции:</p>
 * <ul>
 *     <li>Получение списка всех пользователей</li>
 *     <li>Создание нового пользователя</li>
 *     <li>Обновление данных существующего пользователя</li>
 *     <li>Добавление и удаление друзей у пользователя</li>
 *     <li>Получение списка друзей пользователя</li>
 *     <li>Поиск общих друзей между двумя пользователями</li>
 * </ul>
 *
 * <p>Все операции выполняются через слой: бизнес-логику ({@link UserService}).</p>
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Возвращает список всех зарегистрированных пользователей.
     *
     * @return коллекция объектов типа {@link User}
     */
    @GetMapping
    public Collection<User> findAllUsers() {
        return userService.findAllUsers();
    }

    /**
     * Возвращает список друзей указанного пользователя.
     *
     * @param friendId ID пользователя, чьи друзья запрашиваются
     * @return коллекция объектов типа {@link User}, представляющих друзей
     */
    @GetMapping("/{friendId}/friends")
    public Collection<User> getFriends(@PathVariable Long friendId) {
        return userService.getFriends(friendId);
    }

    /**
     * Возвращает список общих друзей между двумя пользователями.
     *
     * @param friendId ID первого пользователя
     * @param userId   ID второго пользователя
     * @return коллекция объектов типа {@link User}, представляющих общих друзей
     */
    @GetMapping("/{friendId}/friends/common/{userId}")
    public Collection<User> getCommonFriends(@PathVariable Long friendId,
                                             @PathVariable Long userId) {
        return userService.getCommonFriends(friendId, userId);
    }

    /**
     * Создаёт нового пользователя.
     *
     * @param user объект пользователя, переданный в теле запроса
     * @return созданный объект типа {@link User} с присвоенным ID
     */
    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param user объект пользователя с новыми данными
     * @return обновлённый объект типа {@link User}
     */
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * Добавляет друга указанному пользователю.
     *
     * @param userId   ID пользователя, которому добавляется друг
     * @param friendId ID пользователя, которого добавляют в друзья
     */
    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable Long userId,
                          @PathVariable Long friendId) {
        userService.addFriend(userId, friendId);
    }

    /**
     * Удаляет друга у указанного пользователя.
     *
     * @param userId   ID пользователя, у которого удаляется друг
     * @param friendId ID пользователя, которого удаляют из друзей
     */
    @DeleteMapping("/{userId}/friends/{friendId}")
    public void removeFriend(@PathVariable Long userId,
                             @PathVariable Long friendId) {
        userService.removeFriend(userId, friendId);
    }
}

