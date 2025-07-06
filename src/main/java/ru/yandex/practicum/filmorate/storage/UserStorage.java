package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

/**
 * Интерфейс {@code UserStorage} определяет контракт для реализации слоя хранения данных о пользователях.
 *
 * <p>Содержит методы для выполнения базовых операций сущности "Пользователь", таких как:</p>
 * <ul>
 *     <li>Получение списка всех пользователей</li>
 *     <li>Добавление нового пользователя</li>
 *     <li>Обновление данных существующего пользователя</li>
 *     <li>Получение пользователя по его идентификатору</li>
 * </ul>
 *
 * <p>Интерфейс используется в рамках шаблона проектирования Repository и служит абстракцией,
 * которая позволяет легко менять реализацию хранилища (например, на in-memory или базу данных).</p>
 *
 * @see User
 * @see InMemoryUserStorage
 */
public interface UserStorage {
    Collection<User> findAllUsers();
    User createUser(User user);
    User updateUser(User newUser);
    User getUserById(Long id);
}
