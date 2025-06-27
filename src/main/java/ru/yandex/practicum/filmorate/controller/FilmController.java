package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Контроллер для управления фильмами.
 * Обеспечение получения, создания и обновления списка фильмов.
 */
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    /**
     * Возвращение списка всех фильмов, отсортированных по ID.
     * @return коллекция фильмов в порядке возрастания ID.
     */
    @GetMapping
    public Collection<Film> findAllFilms() {
        log.info("GET /films - получение списка всех фильмов");
        return films.values()
                .stream()
                .sorted(Comparator.comparing(Film::getId))
                .collect(Collectors.toList());
    }

    /**
     * Создание нового фильма.
     * Валидация данных фильма.
     * @param film данные фильма для создания.
     * @return созданный фильм с присвоенным ID.
     * @throws ValidationException если данные фильма не проходят валидацию.
     */
    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("POST /films - попытка добавления фильма: {}", film.getName());
        try {
            if (films.values().stream().anyMatch(f -> f.getName().equalsIgnoreCase(film.getName()))) {
                log.error("Такое название уже существует: {}", film.getName());
                throw new DuplicatedDataException("Фильм с таким названием уже существует");
            }
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Фильм создан: ID={}", film.getId());
            return film;
        } catch (RuntimeException e) {
            log.error("Ошибка при создании фильма", e);
            throw e;
        }
    }

    /**
     * Обновление данных существующего фильма.
     * Проверка, что фильм существует и название не дублируется.
     * @param newFilm новые данные фильма.
     * @return обновленный фильм.
     * @throws NotFoundException если фильм с указанным ID не найден.
     * @throws DuplicatedDataException если новое название уже используется.
     * @throws ValidationException если данные не проходят валидацию.
     */
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
            Optional.ofNullable(newFilm.getDescription()).ifPresent(existingFilm::setDescription);
            Optional.ofNullable(newFilm.getReleaseDate()).ifPresent(existingFilm::setReleaseDate);
            Optional.ofNullable(newFilm.getDuration()).ifPresent(existingFilm::setDuration);
            return existingFilm;
        } catch (RuntimeException e) {
            log.error("Ошибка при обновлении фильма", e);
            throw e;
        }
    }

    /**
     * Генерация следующего ID для нового фильма.
     * Поиск максимально-существующего ID и увеличение его на 1.
     * @return следующий доступный ID.
     */
    private long getNextId() {
        return idCounter.getAndIncrement();
    }
}
