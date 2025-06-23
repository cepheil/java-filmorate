package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тестовый класс для проверки {@link UserController}.
 * Содержит набор интеграционных тестов, проверяющих корректность обработки HTTP-запросов при создании пользователей,
 * включая случаи с некорректными данными.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Проверка, что попытка создания пользователя с пустым email возвращает ошибку 400.
     * Поле email помечено аннотацией @NotNull и @Email, поэтому ожидается, что Spring отклонит запрос.
     */
    @Test
    public void testCreateUserWithEmailNullShouldReturnError() throws Exception {
        String json = """
            {
                "email": "null",
                "login": "user1",
                "birthday": "2000-01-01"
            }
        """;
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    /**
     * Проверка, что попытка создания пользователя с некорректным форматом email возвращает ошибку 400.
     * Ожидается, что валидация по аннотации @Email сработает.
     */
    @Test
    public void testCreateUserWithInvalidEmailFormatShouldReturnError() throws Exception {
        String json = """
            {
                "email": "invalid-email.net",
                "login": "user1",
                "birthday": "2000-01-01"
            }
        """;
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    /**
     * Проверка, что попытка создания пользователя с логином, содержащим пробелы, возвращает ошибку 400.
     * Логин не должен содержать пробельных символов согласно аннотации @Pattern.
     */
    @Test
    public void testCreateUserWithLoginContainingSpaceShouldReturnError() throws Exception {
        String json = """
            {
                "email": "user@example.ru",
                "login": "user one",
                "birthday": "2000-01-01"
            }
        """;
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    /**
     * Проверка, что попытка создания пользователя с датой рождения в будущем возвращает ошибку 400.
     * Дата рождения должна быть не позже текущей даты согласно аннотации @PastOrPresent.
     */
    @Test
    public void testCreateUserWithBirthdayInFutureShouldReturnError() throws Exception {
        String json = """
            {
                "email": "user@example.ru",
                "login": "user1",
                "birthday": "3000-01-01"
            }
        """;
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    /**
     * Проверка успешного создания пользователя с корректными данными.
     * Все поля соответствуют требованиям валидации.
     */
    @Test
    public void testCreateUserWithValidDataShouldReturnSuccess() throws Exception {
        String json = """
            {
                "email": "user@example.ru",
                "login": "user1",
                "birthday": "2000-01-01"
            }
        """;
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }
}

