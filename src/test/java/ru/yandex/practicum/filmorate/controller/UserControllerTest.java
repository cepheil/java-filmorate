package ru.yandex.practicum.filmorate.controller;


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
