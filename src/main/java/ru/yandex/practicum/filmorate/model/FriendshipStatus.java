package ru.yandex.practicum.filmorate.model;

/**
 * Перечисление {@code FriendshipStatus} представляет возможные статусы дружбы между двумя пользователями.
 *
 * <p>Используется в модели {@link User} для отслеживания состояния связи "дружба" с другими пользователями.</p>
 *
 * <p>Поддерживаемые статусы:</p>
 * <ul>
 *     <li>{@link #PENDING} — запрос на добавление в друзья отправлен, но не подтвержден</li>
 *     <li>{@link #CONFIRMED} — дружба успешно подтверждена</li>
 * </ul>
 *
 * <p>Пример использования:</p>
 * <pre>{@code
 * Map<Long, FriendshipStatus> friends = new HashMap<>();
 * friends.put(2L, FriendshipStatus.PENDING); // Запрос отправлен пользователю с ID 2
 * friends.put(3L, FriendshipStatus.CONFIRMED); // Подтвержденная дружба с пользователем 3
 * }</pre>
 */
public enum FriendshipStatus {
    /**
     * Статус дружбы: запрос на добавление в друзья отправлен, но не подтвержден.
     */
    PENDING,
    /**
     * Статус дружбы: связь подтверждена, пользователи считаются друзьями.
     */
    CONFIRMED
}
