package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAllUsers();
    User createUser(User user);
    User updateUser(User newUser);
    User getUserById(Long id);
    void addFriend(Long userId, Long friendId);
    void removeFriend(Long userId, Long friendId);
    Collection<User> getFriends(Long userId);
    Collection<User> getCommonFriends(Long userId1, Long userId2);
}
