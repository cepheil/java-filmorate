package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendshipRepository {

    void addFriend(Long userId, Long friendId);

    void confirmFriendship(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Collection<User> getFriends(Long userId);

    Collection<User> getCommonFriends(Long userId, Long otherUserId);

    boolean hasFriendship(Long userId, Long friendId);

}
