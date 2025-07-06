package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.ValidBirthday;
import ru.yandex.practicum.filmorate.annotation.ValidLogin;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

/**
 * Класс, представляющий модель пользователя.
 * Содержит информацию о пользователе, включая имя, email, логин и дату рождения.
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
    @ValidBirthday
    private LocalDate birthday;
    private Collection<Long> friends = new HashSet<>();
}
