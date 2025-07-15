package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;


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
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователи: {} и {}  -  добавлены в друзья", user.getName(), friend.getName());
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
                .stream()
                .map(userStorage::getUserById)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }


    public Collection<User> getCommonFriends(Long id, Long otherId) {
        log.info("/{userId}/friends/common/{otherId} - Получение списка общих друзей пользователя: {} и : {}",
                id, otherId);
        User user = checkUserExists(id);
        User otherUser = checkUserExists(otherId);
        Set<Long> userFriends = user.getFriends();
        Set<Long> otherUserFriends = otherUser.getFriends();
        Set<Long> commonFriendIds = new HashSet<>(userFriends);
        commonFriendIds.retainAll(otherUserFriends);
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
