package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс {@code UserService} содержит бизнес-логику, связанную с пользователями.
 *
 * <p>В текущей реализации:</p>
 * <ul>
 *     <li>Добавление и удаление друзей с поддержкой статусов</li>
 *     <li>Подтверждение дружбы между пользователями</li>
 *     <li>Получение списка друзей пользователя с фильтрацией по статусу</li>
 *     <li>Поиск общих друзей между двумя пользователями (только подтвержденные)</li>
 * </ul>
 *
 * <p>Для работы используется:</p>
 * <ul>
 *     <li>{@link UserStorage} — для получения и изменения данных о пользователях</li>
 * </ul>
 *
 * <h2>Работа с дружбой:</h2>
 * <p>Связь «дружба» имеет следующие статусы:</p>
 * <ul>
 *     <li><b>PENDING</b> — запрос на добавление в друзья отправлен, но не подтвержден</li>
 *     <li><b>CONFIRMED</b> — дружба подтверждена, пользователи считаются друзьями</li>
 * </ul>
 *
 * <p>Методы:</p>
 * <ul>
 *     <li>{@link #addFriend(Long, Long)} — добавляет дружбу со статусом PENDING</li>
 *     <li>{@link #confirmFriendship(Long, Long)} — изменяет статус дружбы на CONFIRMED</li>
 *     <li>{@link #getFriends(Long)} — возвращает только подтвержденных друзей</li>
 *     <li>{@link #getCommonFriends(Long, Long)} — возвращает общих друзей с обоими статусами CONFIRMED</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    /**
     * Добавляет дружбу между двумя пользователями.
     *
     * <p>Статус дружбы у инициатора устанавливается как {@link FriendshipStatus#PENDING},
     * а у получателя — как {@link FriendshipStatus#PENDING} (или #CONFIRMED).</p>
     *
     * @param userId   ID пользователя, которому добавляется друг
     * @param friendId ID пользователя, которого добавляют в друзья
     * @throws NotFoundException если один из пользователей не найден
     */
    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        user.getFriends().put(friendId, FriendshipStatus.PENDING);
        friend.getFriends().put(userId, FriendshipStatus.PENDING);
    }

    /**
     * Удаляет дружбу между двумя пользователями.
     *
     * <p>Удаляет запись о дружбе в обе стороны.</p>
     *
     * @param userId   ID пользователя, у которого удаляется друг
     * @param friendId ID пользователя, которого удаляют из друзей
     * @throws NotFoundException если один из пользователей не найден
     */
    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    /**
     * Возвращает список подтвержденных друзей указанного пользователя.
     *
     * <p>Фильтрует друзей по статусу {@link FriendshipStatus#CONFIRMED}.</p>
     *
     * @param userId ID пользователя, чьи друзья запрашиваются
     * @return коллекция объектов типа {@link User}, представляющих друзей
     * @throws NotFoundException если пользователь не найден
     */
    public Collection<User> getFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        return user.getFriends()
                .entrySet()
                .stream()
                .filter(e -> e.getValue() == FriendshipStatus.CONFIRMED)
                .map(e -> userStorage.getUserById(e.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список общих друзей между двумя пользователями.
     *
     * <p>Учитываются только те друзья, у которых статус дружбы равен
     * {@link FriendshipStatus#CONFIRMED} у обоих пользователей.</p>
     *
     * @param userId1 ID первого пользователя
     * @param userId2 ID второго пользователя
     * @return коллекция объектов типа {@link User}, представляющих общих друзей
     * @throws NotFoundException если один из пользователей не найден
     */
    public Collection<User> getCommonFriends(Long userId1, Long userId2) {
        User user1 = userStorage.getUserById(userId1);
        if (user1 == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        User user2 = userStorage.getUserById(userId2);
        if (user2 == null) {
            throw new NotFoundException("Пользователь не найден.");
        }

        Set<Long> commonIds = user1.getFriends()
                .entrySet()
                .stream()
                .filter(e -> e.getValue() == FriendshipStatus.CONFIRMED)
                .map(Map.Entry::getKey)
                .filter(id -> user2.getFriends().containsKey(id) &&
                user2.getFriends().get(id) == FriendshipStatus.CONFIRMED)
                .collect(Collectors.toSet());

        return commonIds.stream()
                .map(userStorage::getUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Подтверждает дружбу между двумя пользователями.
     *
     * <p>Изменяет статус дружбы на {@link FriendshipStatus#CONFIRMED}
     * у обоих пользователей.</p>
     *
     * @param userId   ID пользователя, который подтверждает дружбу
     * @param friendId ID пользователя, с которым подтверждается дружба
     * @throws NotFoundException если один из пользователей не найден
     */
    public void confirmFriendship(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        if (user.getFriends().containsKey(friendId)) {
            user.getFriends().put(friendId, FriendshipStatus.CONFIRMED);
            friend.getFriends().put(userId, FriendshipStatus.CONFIRMED);
        }
    }

    /**
     * Возвращает список всех пользователей.
     */
    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    /**
     * Возвращает пользователя по ID.
     */
    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    /**
     * Создаёт нового пользователя.
     */
    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    /**
     * Обновляет данные существующего пользователя.
     */
    public User updateUser(User newUser) {
        return userStorage.updateUser(newUser);
    }
}
