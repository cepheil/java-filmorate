package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тестовый класс для проверки {@link FilmController}.
 * Содержит набор интеграционных тестов, проверяющих корректность обработки HTTP-запросов при создании фильмов,
 * включая случаи с некорректными данными.
 */
@WebMvcTest(FilmController.class)
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Проверка, что попытка создания фильма с пустым названием возвращает ошибку 400.
     * Поле name помечено аннотацией @NotBlank, поэтому ожидается, что Spring отклонит запрос.
     */
    @Test
    public void testCreateFilmWithEmptyNameShouldReturnError() throws Exception {
        String json = """
                    {
                        "name": "",
                        "description": "test",
                        "releaseDate": "2000-01-01",
                        "duration": 120
                    }
                """;
        mockMvc.perform(post("/films").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());
    }

    /**
     * Проверка, что попытка создания фильма с датой релиза ранее 28.12.1895 возвращает ошибку 400.
     * Дата релиза не может быть раньше даты рождения кинематографа.
     */
    @Test
    public void testCreateFilmWithInvalidReleaseDateShouldReturnError() throws Exception {
        String json = """
            {
                "name": "Test Film",
                "description": "test",
                "releaseDate": "1895-12-27",
                "duration": 120
            }
        """;
        mockMvc.perform(post("/films").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());
    }

    /**
     * Проверка, что попытка создания фильма с описанием длиннее 200 символов возвращает ошибку 400.
     * Описание должно соответствовать ограничению @Size(max = 200).
     */
    @Test
    public void testCreateFilmWithDescriptionTooLongShouldReturnError() throws Exception {
        String json = """
        {
            "name": "Valid Test Film",
            "description": "%s",
            "releaseDate": "2000-01-01",
            "duration": 120
        }
    """.formatted("d".repeat(201)); // 201 символ — превышает лимит

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    /**
     * Проверка, что попытка создания фильма с продолжительностью <= 0 возвращает ошибку 400.
     * Продолжительность должна быть положительным числом.
     */
    @Test
    public void testCreateFilmWithDurationZeroShouldReturnError() throws Exception {
        String json = """
        {
            "name": "Valid Test Film",
            "description": "test",
            "releaseDate": "2000-01-01",
            "duration": 0
        }
    """;

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    /**
     * Проверка, что попытка создания фильма с датой релиза в будущем возвращает ошибку 400.
     * Дата релиза не может быть в будущем согласно @PastOrPresent.
     */
    @Test
    public void testCreateFilmWithFutureReleaseDateShouldReturnError() throws Exception {
        String json = """
        {
            "name": "Valid Test Film",
            "description": "test",
            "releaseDate": "2100-01-01",
            "duration": 120
        }
    """;

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    /**
     * Проверка, что попытка создания фильма с пустым телом запроса ("{}") возвращает ошибку 400.
     * Все обязательные поля модели Film отсутствуют.
     */
    @Test
    public void testCreateFilmWithEmptyBodyShouldReturnError() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Проверка успешного создания фильма с корректными данными.
     * Все поля соответствуют требованиям валидации.
     */
    @Test
    public void testCreateFilmWithValidDataShouldReturnSuccess() throws Exception {
        String json = """
        {
            "name": "Valid Test Film",
            "description": "test",
            "releaseDate": "2000-01-01",
            "duration": 148
        }
    """;

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }
}
