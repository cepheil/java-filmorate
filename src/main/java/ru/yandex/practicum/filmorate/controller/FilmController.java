package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();
    LocalDate movieBirthDate = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("GET /films - получение списка всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("POST /films добавление фильма: {}", film.getName());
        for (Film f : films.values()) {
            if (f.getName().equals(film.getName())) {
                log.error("Ошибка: Такое название уже существует {}", film.getName());
                throw new DuplicatedDataException("Такое название уже существует");
            }
        }

        if (film.getReleaseDate().isBefore(movieBirthDate)) {
            log.error("Ошибка: дата релиза раньше 28 декабря 1895 года: {}", film.getReleaseDate());
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм: {} , ID: {} , добавлен!", film.getName(), film.getId());
        return film;
    }


    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        log.info("PUT /films - обновление фильма: {}", newFilm.getName());
        if (newFilm.getId() == null) {
            log.error("Ошибка: Id не указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        Film existingFilm = films.get(newFilm.getId());

        if (existingFilm == null) {
            log.error("Ошибка: фильм с Id: {} не найден", newFilm.getId());
            throw new NotFoundException("Фильм с Id = " + newFilm.getId() + " не найден");
        }

        if (newFilm.getReleaseDate().isBefore(movieBirthDate)) {
            log.error("Ошибка: дата релиза раньше 28 декабря 1895 года: {}", newFilm.getReleaseDate());
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }

        if (newFilm.getName() != null && !newFilm.getName().equals(existingFilm.getName())) {
            for (Film f : films.values()) {
                if (!f.getId().equals(existingFilm.getId()) && f.getName().equals(newFilm.getName())) {
                    log.error("Ошибка: Такое название уже существует: {}", newFilm.getName());
                    throw new DuplicatedDataException("Такое название уже существует");
                }
            }
            existingFilm.setName(newFilm.getName());
        }

        if (newFilm.getDescription() != null) {
            existingFilm.setDescription(newFilm.getDescription());
            log.info("Обновлено описание фильма: {}", newFilm.getName());
        }

        if (newFilm.getReleaseDate() != null) {
            existingFilm.setReleaseDate(newFilm.getReleaseDate());
            log.info("Обновлена дата релиза фильма: {}. Новая дата {}", newFilm.getName(), newFilm.getReleaseDate());
        }

        if (newFilm.getDuration() != null) {
            existingFilm.setDuration(newFilm.getDuration());
            log.info("Обновлена длительность фильма: {}. Новая длительность {}", newFilm.getName(), newFilm.getDuration());
        }

        log.info("Фильм с ID {} успешно обновлён", newFilm.getId());
        return existingFilm;
    }


    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


}
