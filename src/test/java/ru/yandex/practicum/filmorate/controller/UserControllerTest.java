package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("Пользователь: Пустой email → 400 Bad Request")
    public void shouldReturnErrorIfEmailIsBlank() throws Exception {
        // CHECKSTYLE:OFF
        String json = """
                    {
                        "email": " ",
                        "login": "Admin",
                        "name": "Bob",
                        "birthday": "2000-01-01"
                    }
                """;
        // CHECKSTYLE:ON
        mockMvc.perform(post("/users").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());

    }


    @Test
    @DisplayName("Пользователь: email = null  → 400 Bad Request")
    public void shouldReturnErrorIfEmailIsNull() throws Exception {
        // CHECKSTYLE:OFF
        String json = """
                    {
                        "email": null,
                        "login": "admin",
                        "name": "Bob",
                        "birthday": "2000-01-01"
                    }
                """;
        // CHECKSTYLE:ON
        mockMvc.perform(post("/users").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());

    }


    @Test
    @DisplayName("Пользователь: email не соответствует формату  → 400 Bad Request")
    public void shouldReturnErrorIfEmailIsNotValid() throws Exception {
        // CHECKSTYLE:OFF
        String json = """
                    {
                        "email": "admin.com",
                        "login": "admin",
                        "name": "Bob",
                        "birthday": "2000-01-01"
                    }
                """;
        // CHECKSTYLE:ON
        mockMvc.perform(post("/users").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());

    }


    @Test
    @DisplayName("Пользователь: login пустой  → 400 Bad Request")
    public void shouldReturnErrorIfLoginIsBlank() throws Exception {
        // CHECKSTYLE:OFF
        String json = """
                    {
                        "email": "admin@mail.com",
                        "login": " ",
                        "name": "Bob",
                        "birthday": "2000-01-01"
                    }
                """;
        // CHECKSTYLE:ON
        mockMvc.perform(post("/users").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());

    }


    @Test
    @DisplayName("Пользователь: login содержит пробелы  → 400 Bad Request")
    public void shouldReturnErrorIfLoginNotValid() throws Exception {
        // CHECKSTYLE:OFF
        String json = """
                    {
                        "email": "admin@mail.com",
                        "login": "Bob Lazar",
                        "name": "Bob",
                        "birthday": "2000-01-01"
                    }
                """;
        // CHECKSTYLE:ON
        mockMvc.perform(post("/users").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Пользователь: birthday = null  → 400 Bad Request")
    public void shouldReturnErrorIfBirthdayNull() throws Exception {
        // CHECKSTYLE:OFF
        String json = """
                    {
                        "email": "admin@mail.com",
                        "login": "Admin",
                        "name": "Bob",
                        "birthday": null
                    }
                """;
        // CHECKSTYLE:ON
        mockMvc.perform(post("/users").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());

    }


    @Test
    @DisplayName("Пользователь: birthday не может быть в будущем  → 400 Bad Request")
    public void shouldReturnErrorIfBirthdayInFuture() throws Exception {
        // CHECKSTYLE:OFF
        String json = """
                    {
                        "email": "admin@mail.com",
                        "login": "Admin",
                        "name": "Bob",
                        "birthday": "2200-01-01"
                    }
                """;
        // CHECKSTYLE:ON
        mockMvc.perform(post("/users").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Пользователь: Валидные данные → 200 OK")
    public void shouldCreateUserWithValidData() throws Exception {
        // CHECKSTYLE:OFF
        String json = """
                    {
                        "email": "admin@mail.com",
                        "login": "Admin123",
                        "name": "Bob",
                        "birthday": "2000-01-01"
                    }
                """;
        // CHECKSTYLE:ON
        mockMvc.perform(post("/users").contentType("application/json").content(json))
                .andExpect(status().isOk());
    }

}
