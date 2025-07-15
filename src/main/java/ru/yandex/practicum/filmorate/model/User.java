package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.ValidLogin;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс {@code User} представляет модель пользователя.
 * Содержит информацию о пользователе, включая имя, email, логин, дату рождения и список друзей.
 *
 * <p>Поля класса:</p>
 * <ul>
 *     <li>{@link #id} — уникальный идентификатор пользователя</li>
 *     <li>{@link #name} — отображаемое имя пользователя (может быть пустым, тогда используется логин)</li>
 *     <li>{@link #email} — адрес электронной почты (обязательное поле, должно соответствовать формату email)</li>
 *     <li>{@link #login} — уникальный логин пользователя (не может быть пустым и не должен содержать пробелов)</li>
 *     <li>{@link #birthday} — дата рождения пользователя (не может быть пустой и не должна быть в будущем)</li>
 *     <li>{@link #friends} — коллекция ID друзей с указанием статуса дружбы</li>
 * </ul>
 *
 * <p>Валидация:</p>
 * <ul>
 *     <li>{@link NotNull} — поле {@code email} обязательно для заполнения</li>
 *     <li>{@link Email} — значение поля {@code email} должно быть корректным</li>
 *     <li>{@link NotNull} — поле {@code login} обязательно для заполнения</li>
 *     <li>{@link ValidLogin} — проверяет, что логин содержит только буквы и цифры и не имеет пробелов</li>
 *     <li>{@link NotNull} — поле {@code birthday} обязательно для заполнения</li>
 *     <li>{@link PastOrPresent} — гарантирует, что дата рождения не в будущем</li>
 * </ul>
 *
 * <h2>Дружба между пользователями</h2>
 * <p>Связь «дружба» имеет следующие статусы:</p>
 * <ul>
 *     <li><b>PENDING</b> — запрос на добавление в друзья отправлен, но не подтвержден</li>
 *     <li><b>CONFIRMED</b> — дружба подтверждена, пользователи считаются друзьями</li>
 * </ul>
 */
@Data
public class User {
    private Long id;
    private String name;
    @NotNull(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;
    @NotNull(message = "Логин не может быть пустым")
    @ValidLogin
    private String login;
    @NotNull(message = "Дата рождения не может быть пустой")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
    private Map<Long, FriendshipStatus> friends = new HashMap<>();
}
