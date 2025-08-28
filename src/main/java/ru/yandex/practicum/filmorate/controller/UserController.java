package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.FriendService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FriendService friendService;
    private final EventService eventService;

    /**
     * Получить список всех пользователей.
     *
     * @return коллекция всех пользователей
     */
    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("Получен запрос на получение списка всех пользователей");
        return userService.findAllUsers();
    }

    /**
     * Получить список друзей пользователя.
     *
     * @param userId идентификатор пользователя
     * @return коллекция друзей пользователя
     */
    @GetMapping("/{userId}/friends")
    public Collection<User> getFriends(@PathVariable Long userId) {
        log.info("Получен запрос на получение списка друзей пользователя с ID: {}", userId);
        return friendService.getFriends(userId);
    }

    /**
     * Получить список общих друзей двух пользователей.
     *
     * @param userId  идентификатор первого пользователя
     * @param otherId идентификатор второго пользователя
     * @return коллекция общих друзей
     */
    @GetMapping("/{userId}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long userId,
                                             @PathVariable Long otherId) {
        log.info("Получен запрос на получение общих друзей  пользователей с ID {} и {}", userId, otherId);
        return friendService.getCommonFriends(userId, otherId);
    }

    /**
     * Получить пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @return пользователь с указанным ID
     * NotFoundException если пользователь не найден
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Получен запрос на получение пользователя с ID: {}", id);
        return userService.getUserById(id);
    }

    /**
     * Создать нового пользователя.
     *
     * @param user объект пользователя для создания
     * @return созданный пользователь с присвоенным ID
     */
    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user.getName());
        return userService.createUser(user);
    }

    /**
     * Обновить существующего пользователя.
     *
     * @param user объект пользователя с обновлёнными данными
     * @return обновлённый пользователь
     */
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя с ID: {}", user.getId());
        return userService.updateUser(user);
    }

    /**
     * Добавить друга пользователю.
     *
     * @param userId   идентификатор пользователя
     * @param friendId идентификатор друга
     */
    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable Long userId,
                          @PathVariable Long friendId) {
        log.info("Получен запрос на добавление друга с ID {} пользователю с ID {}", friendId, userId);
        friendService.addFriend(userId, friendId);
    }

    /**
     * Удалить друга у пользователя.
     *
     * @param userId   идентификатор пользователя
     * @param friendId идентификатор друга
     */
    @DeleteMapping("/{userId}/friends/{friendId}")
    public void removeFriend(@PathVariable Long userId,
                             @PathVariable Long friendId) {
        log.info("Получен запрос на удаление друга с ID {} у пользователя с ID {}", friendId, userId);
        friendService.removeFriend(userId, friendId);
    }

    /**
     * Получить рекомендации фильмов для пользователя.
     *
     * @param userId идентификатор пользователя
     * @return коллекция рекомендованных фильмов
     */
    @GetMapping("/{id}/recommendations")
    public Collection<Film> getRecommendedFilms(@PathVariable("id") Long userId) {
        log.info("Получен запрос на получение рекомендаций фильмов для пользователя с ID: {}", userId);
        return userService.getRecommendedFilms(userId);
    }

    /**
     * Удалить пользователя по ID.
     *
     * @param userId идентификатор пользователя для удаления
     */
    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable Long userId) {
        log.info("Получен запрос на удаление пользователя с ID: {}", userId);
        userService.removeUser(userId);
    }

    /**
     * Получить ленту событий пользователя.
     *
     * @param userId идентификатор пользователя
     * @return коллекция событий пользователя
     */
    @GetMapping("/{userId}/feed")
    public Collection<Event> getEventFeed(@PathVariable Long userId) {
        log.info("Получен запрос на получение ленты событий пользователя с ID: {}", userId);
        return eventService.findEventsByUserId(userId, 0);
    }
}

