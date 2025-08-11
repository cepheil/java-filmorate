package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;


import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository jdbcUserRepository;
    private final EntityValidator entityValidator;


    public User createUser(User user) {
        log.info("POST /users добавление пользователя: {}", user.getName());
        entityValidator.validateUser(user);
        entityValidator.validateUserEmailUniqueness(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User createdUser = jdbcUserRepository.create(user);
        log.info("Пользователь: {} , ID: {} , добавлен!", user.getName(), user.getId());
        return createdUser;
    }


    public User updateUser(User newUser) {
        log.info("PUT /users - обновление пользователя: {}", newUser.getName());
        entityValidator.validateUserForUpdate(newUser);
        User updatedUser = jdbcUserRepository.update(newUser);
        log.info("Пользователь с ID {} успешно обновлён", newUser.getId());
        return updatedUser;
    }


    public Collection<User> getAllUsers() {
        log.info("GET /users - получение списка всех пользователей");
        return jdbcUserRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }


    public User getUserById(Long id) {
        log.info("GET /users/{userId} - получение пользователя по Id");
        if (id == null) {
            log.error("Запрос пользователя с null-ID отклонён");
            throw new ValidationException("ID пользователя не может быть null");
        }
        return jdbcUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID=" + id + " не найден"));
    }


    public void deleteUser(Long id) {
        log.info("DELETE /users/{userId} - удаление пользователя по его ID");
        if (id == null) {
            log.error("Удаление пользователя с null-ID отклонён");
            throw new ValidationException("ID пользователя не может быть null");
        }

        if (!jdbcUserRepository.delete(id)) {
            log.warn("Пользователь с ID={} не найден при попытке удаления", id);
            throw new NotFoundException("Пользователь с ID=" + id + " не найден");
        }
        log.info("Пользователь с ID {} успешно удалён", id);
    }

}

