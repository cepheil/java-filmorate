package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendRepository;
import ru.yandex.practicum.filmorate.storage.user.UserRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public void addFriend(Long userId, Long friendId) {
        log.debug("Попытка добавления друзья: пользователь {} добавляет {}", userId, friendId);
        validateUserExist(userId, friendId);
        if (userId.equals(friendId)) {
            throw new ValidationException("Пользователь не может добавить себя в друзья.");
        }
        friendRepository.addFriend(userId, friendId);
        log.info("Пользователь {} отправил запрос на дружбу пользователю {}", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.debug("Пользователь {} удалил пользователя {} из друзей", userId, friendId);
        validateUserExist(userId, friendId);
        friendRepository.removeFriend(userId, friendId);
        log.info("Пользователь {} удалил пользователя {} из друзей", userId, friendId);
    }

    public Collection<User> getFriends(Long userId) {
        log.debug("Попытка получения списка друзей для пользователя {}", userId);
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        getUserById(userId);
        return friendRepository.getFriends(userId);
    }

    public Collection<User> getCommonFriends(Long userId1, Long userId2) {
        log.debug("Получение общих друзей пользователей {} и {}", userId1, userId2);
        validateUserExist(userId1, userId2);
        return friendRepository.getCommonFriends(userId1, userId2);
    }

    private void validateUserExist(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new ValidationException("ID пользователей не могут быть null.");
        }
        getUserById(userId);
        getUserById(friendId);
    }

    private User getUserById(Long userId) {
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким ID " + userId + " не найден."));
    }
}
