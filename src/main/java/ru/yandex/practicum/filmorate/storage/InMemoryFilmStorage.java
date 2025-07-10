package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final LocalDate movieBirthDate = LocalDate.of(1895, 12, 28);


    @Override
    public Collection<Film> getAllFilms() {
        log.info("GET /films - получение списка всех фильмов");
        return films.values();
    }


    @Override
    public Film getFilmById(Long id) {
        log.info("GET /films/{filmId} - получение списка всех фильмов");
        Film film = films.get(id);
        if (film == null) {
            log.error("Ошибка: фильм с Id: {} не найден", id);
            throw new NotFoundException("Фильм с Id = " + id + " не найден");
        }
        return film;
    }


    @Override
    public Film createFilm(Film film) {
        log.info("POST /films добавление фильма: {}", film.getName());
        for (Film f : films.values()) {
            if (f.getName().equals(film.getName()) && f.getReleaseDate().equals(film.getReleaseDate())) {
                log.error("Ошибка: Такой фильм уже существует {}, релиз: {}", film.getName(), film.getReleaseDate());
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


    @Override
    public Film updateFilm(Film newFilm) {
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
                if (!f.getId().equals(existingFilm.getId()) && f.getName().equals(newFilm.getName()) &&
                        f.getReleaseDate().equals(newFilm.getReleaseDate())) {
                    log.error("Ошибка: Такой фильм уже существует: {}, релиз: {}",
                            newFilm.getName(), newFilm.getReleaseDate());
                    throw new DuplicatedDataException("Такой фильм уже существует");
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
