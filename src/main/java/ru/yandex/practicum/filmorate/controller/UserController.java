package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Контроллер для управления пользователями.
 * Обеспечение получения, создания и обновления списка пользователей.
 */
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    Map<String, User> users = new HashMap<>();

    /**
     * Возвращение списка всех пользователей, отсортированных по ID.
     * @return коллекция пользователей в порядке возрастания ID.
     */
    @GetMapping
    public Collection<User> findAll() {
        log.info("GET /users - получение списка всех пользователей");
        return users.values()
                .stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    /**
     * Создание нового пользователя.
     * Валидация данных пользователя. Если имя пустое, заменяет его логином.
     * @param user данные пользователя для создания.
     * @return созданный пользователь с присвоенным ID.
     * @throws ValidationException если данные пользователя не проходят валидацию.
     */
    @PostMapping
    public User create(@RequestBody User user) {
        log.info("POST /users - попытка создания пользователя: {}", user);
        try {
            validateUser(user);
            if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
                throw new DuplicatedDataException("Email уже используется");
            }
            user.setId(getNextId());
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
                log.debug("Замена пустого имени на логин: {}", user.getLogin());
            }
            users.put(user.getEmail(), user);
            log.info("Пользователь создан: ID={}", user.getId());
            return user;
        } catch (RuntimeException e) {
            log.error("Ошибка при создании пользователя: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Обновление данных существующего пользователя.
     * Проверка, что пользователь существует и email не дублируется.
     * @param newUser новые данные пользователя.
     * @return обновленный пользователь.
     * @throws NotFoundException если пользователь с указанным ID не найден.
     * @throws DuplicatedDataException если новый email уже используется.
     * @throws ValidationException если данные не проходят валидацию.
     */
    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("PUT /users - попытка обновления пользователя: {}", newUser);
        try {
            validateUser(newUser);
            User existingUser = users.values()
                    .stream()
                    .filter(user -> user.getId().equals(newUser.getId()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Пользователь с ID " + newUser.getId() + " не найден"));

            if (!newUser.getEmail().equals(existingUser.getEmail()) && users.containsKey(newUser.getEmail())) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            users.remove(existingUser.getEmail());
            existingUser.setEmail(newUser.getEmail());
            users.put(existingUser.getEmail(), existingUser);
            existingUser.setName(newUser.getName());
            existingUser.setLogin(newUser.getLogin());
            return existingUser;
        } catch (RuntimeException e) {
            log.error("Ошибка при обновлении пользователя: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Валидация данных пользователя.
     * @param user пользователь для валидации.
     * @throws ValidationException если:
     * - email не содержит символ '@'
     * - логин содержит пробелы
     * - дата рождения в будущем.
     */
    private void validateUser(User user) {
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Email должен содержать @");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин должен быть без пробела");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    /**
     * Генерация следующего ID для нового пользователя.
     * Поиск максимально-существующего ID и увеличение его на 1.
     * @return следующий доступный ID.
     */
    private long getNextId() {
        return users.values()
                .stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0) + 1;
    }
}
