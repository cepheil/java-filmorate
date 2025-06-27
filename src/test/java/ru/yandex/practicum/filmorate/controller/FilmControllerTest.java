package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("Фильм: Пустое имя → 400 Bad Request")
    public void shouldReturnErrorIfNameIsBlank() throws Exception {
        // CHECKSTYLE:OFF
        String json = """
                    {
                        "name": "",
                        "description": "valid description",
                        "releaseDate": "2020-01-01",
                        "duration": 100
                    }
                """;
        // CHECKSTYLE:ON
        mockMvc.perform(post("/films").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());

    }


    @Test
    @DisplayName("Фильм: Описание >200 символов → 400 Bad Request")
    public void shouldReturnErrorIfDescriptionTooLong() throws Exception {
        // CHECKSTYLE:OFF
        String json = """
                    {
                        "name": "Film",
                        "description": "%s",
                        "releaseDate": "2020-01-01",
                        "duration": 100
                    }
                """.formatted("a".repeat(201));
        // CHECKSTYLE:ON
        mockMvc.perform(post("/films").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Фильм: Отрицательная продолжительность → 400 Bad Request")
    public void shouldReturnErrorIfDurationIsNegative() throws Exception {
        // CHECKSTYLE:OFF
        String json = """
                    {
                        "name": "Film",
                        "description": "valid description",
                        "releaseDate": "2020-01-01",
                        "duration": -10
                    }
                """;
        // CHECKSTYLE:ON
        mockMvc.perform(post("/films").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Фильм: Продолжительность = 0 → 400 Bad Request")
    public void shouldReturnErrorIfDurationIsZero() throws Exception {
        // CHECKSTYLE:OFF
        String json = """
                    {
                        "name": "Film",
                        "description": "valid description",
                        "releaseDate": "2020-01-01",
                        "duration": 0
                    }
                """;
        // CHECKSTYLE:ON
        mockMvc.perform(post("/films").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Фильм: Валидные данные → 200 OK")
    public void shouldPassWithValidData() throws Exception {
        // CHECKSTYLE:OFF
        String json = """
                    {
                        "name": "Film",
                        "description": "valid description",
                        "releaseDate": "2020-01-01",
                        "duration": 100
                    }
                """;
        // CHECKSTYLE:ON
        mockMvc.perform(post("/films").contentType("application/json").content(json))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("Фильм: Дата релиза до 28.12.1895 → 400 Bad Request")
    public void shouldReturnErrorIfReleaseDateBefore1895() throws Exception {
        // CHECKSTYLE:OFF
        String json = """
                    {
                        "name": "Film",
                        "description": "valid description",
                        "releaseDate": "1895-12-27",
                        "duration": 100
                    }
                """;
        // CHECKSTYLE:ON
        Exception resolved = assertThrows(Exception.class, () -> {
            mockMvc.perform(post("/films")
                            .contentType("application/json")
                            .content(json))
                    .andReturn();
        });

    }


    @Test
    @DisplayName("Фильм: Пустое тело запроса → 400 Bad Request")
    public void shouldReturnErrorIfEmptyBody() throws Exception {
        // CHECKSTYLE:OFF
        String json = "{}";
        // CHECKSTYLE:ON
        mockMvc.perform(post("/films").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());

    }


}
