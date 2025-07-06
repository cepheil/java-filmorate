package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public Collection<Film> findAllFilms() {
        log.info("GET /films - получение списка всех фильмов.");
        return films.values()
                .stream()
                .sorted(Comparator.comparing(Film::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Film createFilm(Film film) {
        log.info("POST /films - попытка добавления фильма: {}", film.getName());
        if (films.values()
                .stream()
                .anyMatch(f -> f.getName().equalsIgnoreCase(film.getName()))) {
            log.error("Такое название уже существует: {}", film.getName());
            throw new DuplicatedDataException("Фильм с таким названием уже существует.");
        }
            film.setId(idCounter.getAndIncrement());
            films.put(film.getId(), film);
            log.info("Фильм создан: ID={}", film.getId());
            return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        log.info("PUT /films - попытка обновления фильма: {}", newFilm.getName());
        Film existingFilm = films.get(newFilm.getId());
        if (existingFilm == null) {
            log.error("Отсутствует фильм с ID: {}", newFilm.getId());
            throw new NotFoundException("Фильм с ID " + newFilm.getId() + " не найден.");
        }
        if (!newFilm.getName().equalsIgnoreCase(existingFilm.getName()) &&
        films.values()
                .stream()
                .anyMatch(film -> film.getName().equalsIgnoreCase(newFilm.getName()))) {
            log.error("Такое название уже существует: {}", newFilm.getName());
            throw new DuplicatedDataException("Фильм с таким названием уже существует.");
        }
        existingFilm.setName(newFilm.getName());
        Optional.ofNullable(newFilm.getDescription()).ifPresent(existingFilm::setDescription);
        Optional.ofNullable(newFilm.getReleaseDate()).ifPresent(existingFilm::setReleaseDate);
        Optional.ofNullable(newFilm.getDuration()).ifPresent(existingFilm::setDuration);
        return existingFilm;
    }

    @Override
    public Film getFilmById(Long id) {
        return films.get(id);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return films.values()
                .stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
