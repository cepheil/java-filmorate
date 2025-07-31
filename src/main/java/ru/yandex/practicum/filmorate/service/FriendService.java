package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final ValidationService validationService;

    public void addFriend(Long userId, Long friendId) {
        log.debug("Попытка добавления друзья: пользователь {} добавляет {}", userId, friendId);
        validationService.validateUsersExist(userId, friendId);
        if (userId.equals(friendId)) {
            throw new ValidationException("Пользователь не может добавить себя в друзья.");
        }
        friendRepository.addFriend(userId, friendId);
        log.info("Пользователь {} отправил запрос на дружбу пользователю {}", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.debug("Пользователь {} удалил пользователя {} из друзей", userId, friendId);
        validationService.validateUsersExist(userId, friendId);
        friendRepository.removeFriend(userId, friendId);
        log.info("Пользователь {} удалил пользователя {} из друзей", userId, friendId);
    }

    public Collection<User> getFriends(Long userId) {
        log.debug("Попытка получения списка друзей для пользователя {}", userId);
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        return friendRepository.getFriends(userId);
    }

    public Collection<User> getCommonFriends(Long userId1, Long userId2) {
        log.debug("Получение общих друзей пользователей {} и {}", userId1, userId2);
        validationService.validateUsersExist(userId1, userId2);
        return friendRepository.getCommonFriends(userId1, userId2);
    }
}
