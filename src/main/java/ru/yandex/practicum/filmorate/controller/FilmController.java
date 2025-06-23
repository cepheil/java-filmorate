package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAllFilms() {
        log.info("GET /films - получение списка всех фильмов");
        return films.values()
                .stream()
                .sorted(Comparator.comparing(Film::getId))
                .collect(Collectors.toList());
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("POST /films - попытка добавления фильма: {}", film.getName());
        try {
            if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
                log.error("Фильм создан раньше установленной даты: {}", film.getReleaseDate());
                throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
            }
            if (films.values().stream().anyMatch(f -> f.getName().equalsIgnoreCase(film.getName()))) {
                log.error("Такое название уже существует: {}", film.getName());
                throw new DuplicatedDataException("Фильм с таким названием уже существует");
            }
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Фильм создан: ID={}", film.getId());
            return film;
        } catch (RuntimeException e) {
            log.error("Ошибка при создании фильма: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        log.info("PUT /films - попытка обновления фильма {}", newFilm.getName());
        try {
            Film existingFilm = films.get(newFilm.getId());
            if (existingFilm == null) {
                log.error("Отсутствует фильм с ID: {}", newFilm.getId());
                throw new NotFoundException("Фильм с ID " + newFilm.getId() + " не найден");
            }
            if (!newFilm.getName().equalsIgnoreCase(existingFilm.getName())
            && films.values().stream().anyMatch(film -> film.getName().equalsIgnoreCase(newFilm.getName()))) {
                log.error("Такое название уже существует: {}", newFilm.getName());
                throw new DuplicatedDataException("Фильм с таким названием уже существует");
            }
            existingFilm.setName(newFilm.getName());
            existingFilm.setDescription(newFilm.getDescription());
            existingFilm.setReleaseDate(newFilm.getReleaseDate());
            existingFilm.setDuration(newFilm.getDuration());
            return existingFilm;
        } catch (RuntimeException e) {
            log.error("Ошибка при обновлении фильма {}", e.getMessage());
            throw e;
        }
    }


    private long getNextId() {
        return idCounter.getAndIncrement();
    }
}
