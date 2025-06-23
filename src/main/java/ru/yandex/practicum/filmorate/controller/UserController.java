package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Контроллер для управления пользователями.
 * Обеспечение получения, создания и обновления списка пользователей.
 */
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    /**
     * Возвращение списка всех пользователей, отсортированных по ID.
     * @return коллекция пользователей в порядке возрастания ID.
     */
    @GetMapping
    public Collection<User> findAllUsers() {
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
    public User createUser(@Valid @RequestBody User user) {
        log.info("POST /users - попытка создания пользователя: {}", user.getEmail());
        try {
            if (users.values().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()))) {
                log.error("Такой email уже существует: {}", user.getEmail());
                throw new DuplicatedDataException("Email уже используется");
            }
            if (users.containsKey(user.getId())) {
                log.error("Такой ID уже существует: {}", user.getId());
                throw new DuplicatedDataException("Пользователь с таким ID уже существует");
            }
            if (user.getLogin().contains(" ")) {
                log.error("Логин содержит пробелы: {}", user.getLogin());
                throw new ValidationException("Логин не может содержать пробелы");
            }
            user.setId(getNextId());
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
                log.debug("Замена пустого имени на логин: {}", user.getLogin());
            }
            users.put(user.getId(), user);
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
    public User updateUser(@Valid @RequestBody User newUser) {
        log.info("PUT /users - попытка обновления пользователя: {}", newUser.getEmail());
        try {
            User existingUser = users.get(newUser.getId());
            if (existingUser == null) {
                log.error("Отсутствует ID: {}", newUser.getId());
                throw new NotFoundException("Пользователь с ID " + newUser.getId() + " не найден");
            }
            if (!newUser.getEmail().equals(existingUser.getEmail())
                    && users.values().stream().anyMatch(user -> user.getEmail().equals(newUser.getEmail()))) {
                log.error("Такой email уже существует: {}", newUser.getEmail());
                throw new DuplicatedDataException("Email уже используется");
            }
            if (newUser.getLogin().contains(" ")) {
                log.error("Логин содержит пробелы: {}", newUser.getLogin());
                throw new ValidationException("Логин не может содержать пробелы");
            }
            existingUser.setEmail(newUser.getEmail());
            existingUser.setLogin(newUser.getLogin());
            existingUser.setName(newUser.getName());
            existingUser.setBirthday(newUser.getBirthday());
            return existingUser;
        } catch (RuntimeException e) {
            log.error("Ошибка при обновлении пользователя: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Генерация следующего ID для нового пользователя.
     * Поиск максимально-существующего ID и увеличение его на 1.
     * @return следующий доступный ID.
     */
    private long getNextId() {
        return idCounter.getAndIncrement();
    }
}
