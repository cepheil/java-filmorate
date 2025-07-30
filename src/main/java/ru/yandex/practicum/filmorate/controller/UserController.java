package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{friendId}/friends")
    public Collection<User> getFriends(@PathVariable Long friendId) {
        return userService.getFriends(friendId);
    }

    @GetMapping("/{friendId}/friends/common/{userId}")
    public Collection<User> getCommonFriends(@PathVariable Long friendId,
                                             @PathVariable Long userId) {
        return userService.getCommonFriends(friendId, userId);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable Long userId,
                          @PathVariable Long friendId) {
        userService.addFriend(userId, friendId);
    }

    @PutMapping("/{userId}/friends/{friendId}/confirm")
    public void confirmFriend(@PathVariable Long userId,
                              @PathVariable Long friendId) {
        userService.confirmFriendship(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void removeFriend(@PathVariable Long userId,
                             @PathVariable Long friendId) {
        userService.removeFriend(userId, friendId);
    }
}

