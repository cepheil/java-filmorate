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
 *     <li>Добавление, удаление и подтверждение дружбы между пользователями</li>
 *     <li>Получение списка друзей пользователя (только подтвержденных)</li>
 *     <li>Поиск общих друзей между двумя пользователями (только подтвержденные)</li>
 * </ul>
 *
 * <p>Все операции выполняются через слой бизнес-логики: {@link UserService}.</p>
 *
 * <h2>Работа с дружбой:</h2>
 * <p>Связь «дружба» имеет следующие статусы:</p>
 * <ul>
 *     <li><b>PENDING</b> — запрос на добавление в друзья отправлен, но не подтвержден</li>
 *     <li><b>CONFIRMED</b> — дружба подтверждена, пользователи считаются друзьями</li>
 * </ul>
 *
 * <p>Доступные эндпоинты:</p>
 * <ul>
 *     <li>{@code PUT /users/{userId}/friends/{friendId}} — отправка запроса на дружбу (статус PENDING)</li>
 *     <li>{@code PUT /users/{userId}/friends/{friendId}/confirm} — подтверждение запроса (статус CONFIRMED)</li>
 *     <li>{@code DELETE /users/{userId}/friends/{friendId}} — удаление дружбы</li>
 * </ul>
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
     * <p>Доступ по адресу: {@code GET /users}</p>
     *
     * @return коллекция объектов типа {@link User}
     */
    @GetMapping
    public Collection<User> findAllUsers() {
        return userService.findAllUsers();
    }

    /**
     * Возвращает список подтвержденных друзей указанного пользователя.
     *
     * <p>Доступ по адресу: {@code GET /users/{friendId}/friends}</p>
     *
     * <p>Возвращаются только те пользователи, у которых статус дружбы равен #CONFIRMED.</p>
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
     * <p>Доступ по адресу: {@code GET /users/{friendId}/friends/common/{userId}}</p>
     *
     * <p>Учитываются только те друзья, у которых статус дружбы равен #CONFIRMED у обоих пользователей.</p>
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
     * Возвращает зарегистрированного пользователя по ID.
     *
     * <p>Доступ по адресу: {@code GET /users/id}</p>
     *
     * @return коллекция объектов типа {@link User}
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     * Создаёт нового пользователя.
     *
     * <p>Доступ по адресу: {@code POST /users}</p>
     *
     * <p>Если поле {@code name} пустое, оно автоматически заменяется на значение поля {@code login}.</p>
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
     * <p>Доступ по адресу: {@code PUT /users}</p>
     *
     * @param user объект пользователя с новыми данными
     * @return обновлённый объект типа {@link User}
     */
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * Отправляет запрос на добавление друга.
     *
     * <p>Доступ по адресу: {@code PUT /users/{userId}/friends/{friendId}}</p>
     *
     * <p>Статус дружбы у инициатора и получателя устанавливаются, как #PENDING.</p>
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
     * Подтверждает запрос на дружбу.
     *
     * <p>Доступ по адресу: {@code PUT /users/{userId}/friends/{friendId}/confirm}</p>
     *
     * <p>Изменяет статус дружбы на #CONFIRMED у обоих пользователей.</p>
     *
     * @param userId   ID пользователя, который подтверждает дружбу
     * @param friendId ID пользователя, с которым подтверждается дружба
     */
    @PutMapping("/{userId}/friends/{friendId}/confirm")
    public void confirmFriend(@PathVariable Long userId,
                              @PathVariable Long friendId) {
        userService.confirmFriendship(userId, friendId);
    }

    /**
     * Удаляет дружбу между двумя пользователями.
     *
     * <p>Доступ по адресу: {@code DELETE /users/{userId}/friends/{friendId}}</p>
     *
     * <p>Удаляет запись о дружбе в обе стороны.</p>
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

