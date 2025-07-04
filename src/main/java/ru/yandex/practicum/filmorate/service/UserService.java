package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            log.warn("Попытка пользователя ID={} добавить себя в друзья", userId);
            throw new ValidationException("Нельзя добавить себя в друзья");
        }
        userStorage.addFriend(userId, friendId);
        log.info("Пользователь ID={} и пользователь ID={} теперь друзья", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        userStorage.removeFriend(userId, friendId);
        log.info("Пользователь ID={} и пользователь ID={} больше не друзья", userId, friendId);
    }

    public Collection<User> getFriends(Long userId) {
        Collection<User> friends = userStorage.getFriends(userId);
        log.debug("Запрошен список друзей пользователя ID={} (всего: {})", userId, friends.size());
        return friends;
    }

    public Collection<User> getCommonFriends(Long userId1, Long userId2) {
        Collection<User> commonFriends = userStorage.getCommonFriends(userId1, userId2);
        log.debug("Найдено {} общих друзей между ID={} и ID={}", commonFriends.size(), userId1, userId2);
        return commonFriends;
    }
}
