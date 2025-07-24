package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.FriendshipStatus.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;


    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers()
                .stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }


    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }


    public User createUser(User user) {
        return userStorage.createUser(user);
    }


    public User updateUser(User newUser) {
        return userStorage.updateUser(newUser);
    }


    public void addFriend(Long userId, Long friendId) {
        log.info("PUT /users/{id}/friends/{friendId} - добавление в друзья к пользователю: {}, друга: {}",
                userId, friendId);
        User user = checkUserExists(userId);
        User friend = checkUserExists(friendId);

        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить самого себя в друзья.");
        }


        if (user.getFriends().containsKey(friendId)) {
            FriendshipStatus status = user.getFriends().get(friendId);
            if (status == PENDING) {
                log.warn("Запрос на дружбу уже отправлен пользователю {}", friendId);
                return;
            }
            if (status == CONFIRMED) {
                log.warn("Пользователи {} и {} уже друзья", userId, friendId);
                return;
            }
        }

        if (friend.getFriends().containsKey(userId) && friend.getFriends().get(userId) == PENDING) {
            user.getFriends().put(friendId, CONFIRMED);
            friend.getFriends().put(userId, CONFIRMED);

            log.info("Дружба между {} и {} подтверждена", userId, friendId);
        } else {
            user.getFriends().put(friendId, PENDING);
            friend.getFriends().put(userId, REQUEST_RECEIVED);
            log.info("Пользователь {} отправил запрос на дружбу пользователю {}", user.getName(), friend.getName());
        }

    }

    public void confirmFriend(Long userId, Long friendId) {
        User user = checkUserExists(userId);
        User friend = checkUserExists(friendId);

        // Проверка существования запроса
        if (!user.getFriends().containsKey(friendId)
                || user.getFriends().get(friendId) != REQUEST_RECEIVED) {
            throw new ConditionsNotMetException("Запрос на дружбу не найден");
        }

        user.getFriends().put(friendId, CONFIRMED);
        friend.getFriends().put(userId, CONFIRMED);

        log.info("Пользователь {} подтвердил дружбу с {}", userId, friendId);
    }


    public void removeFriend(Long userId, Long friendId) {
        log.info("DELETE /users/{id}/friends/{friendId} - Удаление из друзей пользователя: {}, друга: {}",
                userId, friendId);
        User user = checkUserExists(userId);
        User friend = checkUserExists(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователи: {} и {}  -  уже не друзья", user.getName(), friend.getName());
    }


    public Collection<User> getFriendsById(Long id) {
        log.info("GET /users/{id}/friends - Получение списка друзей пользователя: {}", id);
        User user = checkUserExists(id);
        return user.getFriends()
                .entrySet()
                .stream()
                .filter(e -> e.getValue() == CONFIRMED)
                .map(e -> userStorage.getUserById(e.getKey()))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());
    }


    public Collection<User> getCommonFriends(Long id, Long otherId) {
        log.info("/{userId}/friends/common/{otherId} - Получение списка общих друзей пользователя: {} и : {}",
                id, otherId);
        User user = checkUserExists(id);
        User otherUser = checkUserExists(otherId);

        Set<Long> confirmedFriendsOfUser = user.getFriends().entrySet().stream()
                .filter(e -> e.getValue() == CONFIRMED)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Set<Long> confirmedFriendsOfOther = otherUser.getFriends().entrySet().stream()
                .filter(e -> e.getValue() == CONFIRMED)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Set<Long> commonFriendIds = new HashSet<>(confirmedFriendsOfUser);
        commonFriendIds.retainAll(confirmedFriendsOfOther);
        return commonFriendIds.stream()
                .map(userStorage::getUserById)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }


    private User checkUserExists(Long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            log.error("Ошибка: Пользователь с Id = {}  не найден ", id);
            throw new NotFoundException("Пользователь с Id = " + id + " не найден");
        }
        return user;
    }

}
