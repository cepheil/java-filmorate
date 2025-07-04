package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);
    @Override
    public Collection<User> findAllUsers() {
        log.info("GET /users - получение списка всех пользователей.");
        return users.values()
                .stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public User createUser(User user) {
        log.info("POST /users - попытка добавления пользователя: {}", user.getEmail());
        if (users.values()
                .stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()))) {
            log.error("Такой email уже существует: {}", user.getEmail());
        throw new DuplicatedDataException("Email уже используется.");
    }
        user.setId(idCounter.getAndIncrement());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Замена пустого имени на логин: {}", user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь создан ID={}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        log.info("PUT /users - попытка обновления пользователя: {}", newUser.getEmail());
        User existingUser = users.get(newUser.getId());
        if (existingUser == null) {
            log.error("Отсутствует пользователь с ID: {}", newUser.getId());
            throw new NotFoundException("Пользователь с ID " + newUser.getId() + " не найден.");
        }
        if (!newUser.getEmail().equalsIgnoreCase(existingUser.getEmail()) &&
        users.values()
                .stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(newUser.getEmail()))) {
            log.error("Такой email уже существует: {}", newUser.getEmail());
            throw new DuplicatedDataException("Email уже используется.");
        }
        existingUser.setEmail(newUser.getEmail());
        existingUser.setLogin(newUser.getLogin());
        existingUser.setBirthday(newUser.getBirthday());
        if (newUser.getName() != null && !existingUser.getName().isBlank()) {
            existingUser.setName(newUser.getName());
        } else {
            existingUser.setName(newUser.getLogin());
        }
        return existingUser;
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        return user.getFriends()
                .stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getCommonFriends(Long userId1, Long userId2) {
        User user1 = users.get(userId1);
        User user2 = users.get(userId2);
        if (user1 == null || user2 == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        return user1.getFriends()
                .stream()
                .filter(user2.getFriends()::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }
}
