package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса {@link UserStorage}, представляющая собой временное хранилище пользователей в памяти.
 *
 * <p>Этот класс предоставляет реализацию CRUD-операций над сущностью {@link User} и хранит данные в
 * структуре {@link HashMap}, что делает его подходящим для временного использования или тестирования.</p>
 *
 * <h2>Основные функции:</h2>
 * <ul>
 *     <li>Добавление нового пользователя</li>
 *     <li>Обновление данных существующего пользователя</li>
 *     <li>Получение списка всех пользователей</li>
 *     <li>Получение пользователя по ID</li>
 * </ul>
 *
 * <p>Класс также проверяет уникальность email и автоматически заменяет пустое имя на логин, если это необходимо.</p>
 *
 * <p><b>Важно:</b> Данные, хранящиеся в этом хранилище, не сохраняются между запусками приложения.</p>
 *
 * @see UserStorage
 * @see User
 * @see DuplicatedDataException
 * @see NotFoundException
 */
@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    /**
     * Возвращает список всех пользователей, отсортированных по идентификатору.
     *
     * @return коллекция объектов типа {@link User}
     */
    @Override
    public Collection<User> findAllUsers() {
        log.info("GET /users - получение списка всех пользователей.");
        return users.values()
                .stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    /**
     * Добавляет нового пользователя в хранилище.
     *
     * <p>Перед добавлением проверяет уникальность email. Если имя пользователя не указано,
     * оно заменяется на логин.</p>
     *
     * @param user объект пользователя, который необходимо добавить
     * @return объект {@link User} с присвоенным ID
     * @throws DuplicatedDataException если email уже используется другим пользователем
     */
    @Override
    public User createUser(User user) {
        log.info("POST /users - попытка добавления пользователя: {}", user.getEmail());
        validateUniqueUserEmail(user);
        user.setId(idCounter.getAndIncrement());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Замена пустого имени на логин: {}", user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь создан ID={}", user.getId());
        return user;
    }

    /**
     * Обновляет данные существующего пользователя.
     *
     * <p>Проверяет наличие пользователя по ID и уникальность email (если он изменился).</p>
     *
     * @param newUser объект пользователя с обновлёнными данными
     * @return обновлённый объект {@link User}
     * @throws NotFoundException       если пользователь с указанным ID не найден
     * @throws DuplicatedDataException если новый email уже занят
     */
    @Override
    public User updateUser(User newUser) {
        log.info("PUT /users - попытка обновления пользователя: {}", newUser.getEmail());
        User existingUser = users.get(newUser.getId());
        if (existingUser == null) {
            log.error("Отсутствует пользователь с ID: {}", newUser.getId());
            throw new NotFoundException("Пользователь с ID " + newUser.getId() + " не найден.");
        }
        if (!newUser.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            validateUniqueUserEmail(newUser);
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

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return объект {@link User}, если найден, иначе null
     */
    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public boolean deleteUser(Long id) {
        User deletedUser = users.remove(id);
        return deletedUser != null;
    }

    /**
     * Проверяет, что пользователь с таким email ещё не существует.
     *
     * @param user объект пользователя для проверки
     * @throws DuplicatedDataException если название занято
     */
    private void validateUniqueUserEmail(User user) {
        if (users.values()
                .stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()))) {
            log.error("Такой email уже существует: {}", user.getEmail());
            throw new DuplicatedDataException("Email уже используется.");
        }
    }
}
