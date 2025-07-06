package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Класс {@code UserService} содержит бизнес-логику, связанную с пользователями.
 *
 * <p>В текущей реализации:</p>
 * <ul>
 *     <li>Добавление и удаление друзей</li>
 *     <li>Получение списка друзей пользователя</li>
 *     <li>Поиск общих друзей между двумя пользователями</li>
 * </ul>
 *
 * <p>Для работы использует:</p>
 * <ul>
 *     <li>{@link UserStorage} — для получения и изменения данных о пользователях</li>
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
     * @param userId   ID пользователя, которому добавляется друг
     * @param friendId ID пользователя, которого добавляют в друзья
     * @throws NotFoundException если один из пользователей не найден
     */
    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    /**
     * Удаляет дружбу между двумя пользователями.
     *
     * @param userId   ID пользователя, у которого удаляется друг
     * @param friendId ID пользователя, которого удаляют из друзей
     * @throws NotFoundException если один из пользователей не найден
     */
    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    /**
     * Возвращает список друзей указанного пользователя.
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
                .stream()
                .map(userStorage::getUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список общих друзей между двумя пользователями.
     *
     * @param userId1 ID первого пользователя
     * @param userId2 ID второго пользователя
     * @return коллекция объектов типа {@link User}, представляющих общих друзей
     * @throws NotFoundException если один из пользователей не найден
     */
    public Collection<User> getCommonFriends(Long userId1, Long userId2) {
        User user1 = userStorage.getUserById(userId1);
        User user2 = userStorage.getUserById(userId2);
        if (user1 == null || user2 == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        return user1.getFriends()
                .stream()
                .filter(user2.getFriends()::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}
