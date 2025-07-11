package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;


import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }


    @GetMapping("/{userId}")
    public User getUserById(@PathVariable @Positive Long userId) {
        return userService.getUserById(userId);
    }


    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }


    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        return userService.updateUser(newUser);
    }


    //PUT /users/{id}/friends/{friendId}
    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable @Positive Long userId,
                          @PathVariable @Positive Long friendId) {
        userService.addFriend(userId, friendId);
    }


    //DELETE /users/{id}/friends/{friendId}
    @DeleteMapping("/{userId}/friends/{friendId}")
    public void removeFriend(@PathVariable @Positive Long userId,
                             @PathVariable @Positive Long friendId) {
        userService.removeFriend(userId, friendId);
    }


    //GET /users/{id}/friends
    @GetMapping("/{userId}/friends")
    public Collection<User> getFriendsById(@PathVariable @Positive Long userId) {
        return userService.getFriendsById(userId);
    }


    //GET /users/{id}/friends/common/{otherId}
    @GetMapping("/{userId}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable @Positive Long userId,
                                             @PathVariable @Positive Long otherId) {
        return userService.getCommonFriends(userId, otherId);

    }


}


