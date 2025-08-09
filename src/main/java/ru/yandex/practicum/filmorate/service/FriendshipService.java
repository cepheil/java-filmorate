package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FriendshipRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final EntityValidator entityValidator;
    private final FriendshipRepository jdbcFriendshipRepository;


    public void addFriend(Long userId, Long friendId) {
        log.info("PUT /users/{id}/friends/{friendId} - добавление в друзья к пользователю: {}, друга: {}",
                userId, friendId);

        entityValidator.validateFriendshipOperation(userId, friendId); // Наличие в базе обоих U. и U!=F
        jdbcFriendshipRepository.addFriend(userId, friendId);
        log.info("Запрос на дружбу отправлен от {} к {}", userId, friendId);
    }


    public void confirmFriend(Long userId, Long friendId) {
        log.info("PUT /users/{userId}/friends/{friendId}/confirm  Подтверждение дружбы между {}  и  {}",
                userId, friendId);
        entityValidator.validateFriendshipOperation(userId, friendId); // Наличие в базе обоих U. и U!=F
        jdbcFriendshipRepository.confirmFriend(userId, friendId);
        log.info("Пользователь {} подтвердил дружбу с {}", userId, friendId);
    }


    public void removeFriend(Long userId, Long friendId) {
        log.info("DELETE /users/{id}/friends/{friendId} - Удаление из друзей пользователя: {}, друга: {}",
                userId, friendId);
        entityValidator.validateFriendshipOperation(userId, friendId);
        entityValidator.validateFriendshipExists(userId, friendId);
        jdbcFriendshipRepository.removeFriend(userId, friendId);
        log.info("Пользователи: {} и {}  -  уже не друзья", userId, friendId);
    }


    public Collection<User> getFriendsById(Long id) {
        log.info("GET /users/{id}/friends - Получение списка друзей пользователя: {}", id);
        entityValidator.validateUserExists(id);
        return jdbcFriendshipRepository.getFriends(id);
    }


    public Collection<User> getCommonFriends(Long id, Long otherId) {
        log.info("/{userId}/friends/common/{otherId} - Получение списка общих друзей пользователя: {} и : {}",
                id, otherId);
        entityValidator.validateFriendshipOperation(id, otherId);
        return jdbcFriendshipRepository.getCommonFriends(id, otherId);
    }


}
