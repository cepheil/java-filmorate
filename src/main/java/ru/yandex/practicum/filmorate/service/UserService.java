package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendService friendService;

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден."));
        User friend = userStorage.getUserById(friendId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден."));
        friendService.addFriend(user_id, friend_id);
    }

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

    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User newUser) {
        return userStorage.updateUser(newUser);
    }
}
