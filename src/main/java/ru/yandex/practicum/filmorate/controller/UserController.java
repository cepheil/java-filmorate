package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);


    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("GET /users - получение списка всех пользователей");
        return users.values();
    }


    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("POST /users добавление пользователя: {}", user.getName());

        if (user.getEmail() == null) {
            log.error("Ошибка: Email не может быть пустым");
            throw new ValidationException("Email не может быть пустым");
        }
        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail())) {
                log.error("Ошибка: Этот Email уже используется {}", user.getEmail());
                throw new DuplicatedDataException("Этот Email уже используется");
            }
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь: {} , ID: {} , добавлен!", user.getName(), user.getId());
        return user;
    }


    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        log.info("PUT /users - обновление пользователя: {}", newUser.getName());
        if (newUser.getId() == null) {
            log.error("Ошибка: Id не указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        User existingUser = users.get(newUser.getId());

        if (existingUser == null) {
            log.error("Ошибка: Пользователь с Id: {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с Id = " + newUser.getId() + " не найден");
        }

        if (newUser.getEmail() != null && !newUser.getEmail().equals(existingUser.getEmail())) {
            for (User u : users.values()) {
                if (!u.getId().equals(existingUser.getId()) && u.getEmail().equals(newUser.getEmail())) {
                    log.error("Ошибка: Этот Email уже используется {}", newUser.getEmail());
                    throw new DuplicatedDataException("Этот Email уже используется");
                }
            }
            existingUser.setEmail(newUser.getEmail());
        }

        if (newUser.getLogin() != null) {
            existingUser.setLogin(newUser.getLogin());
            log.info("Обновлен логин пользователя: {}", newUser.getLogin());
        }

        if (newUser.getName() != null) {
            existingUser.setName(newUser.getName());
            log.info("Обновлено имя пользователя: {}", newUser.getName());
        }

        if (newUser.getBirthday() != null) {
            existingUser.setBirthday(newUser.getBirthday());
            log.info("Обновлена дата рождения пользователя: {}", newUser.getBirthday());
        }

        log.info("Пользователь с ID {} успешно обновлён", newUser.getId());
        return existingUser;
    }


    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}


