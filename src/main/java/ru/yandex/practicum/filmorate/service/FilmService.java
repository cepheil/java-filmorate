package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms()
                .stream()
                .sorted(Comparator.comparing(Film::getId))
                .collect(Collectors.toList());
    }


    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }


    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }


    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }


    public Collection<Film> getPopularFilms(int count) {
        log.info("//GET /films/popular?count={count}  Получение списка из {} популярных фильмов", count);
        return filmStorage.getAllFilms()
                .stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }


    public void addLike(Long filmId, Long userId) {
        log.info("//PUT /films/{id}/like/{userId}  Добавление лайка фильму ID: {}, от пользователем ID: {}",
                filmId, userId);
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            log.error("Фильм с Id =  {} не найден", filmId);
            throw new NotFoundException("Фильм с Id = " + filmId + " не найден");
        }
        if (userStorage.getUserById(userId) == null) {
            log.error("Пользователь с Id =  {} не найден", userId);
            throw new NotFoundException("Пользователь с Id = " + userId + " не найден");
        }
        film.getLikes().add(userId);
    }


    public void removeLike(Long filmId, Long userId) {
        log.info("//DELETE /films/{id}/like/{userId}  Удаление лайка у фильма ID: {}, от пользователя ID: {}",
                filmId, userId);
        if (filmStorage.getFilmById(filmId) == null) {
            log.error("Фильм с Id =  {} не найден", filmId);
            throw new NotFoundException("Фильм с Id = " + filmId + " не найден");
        }
        if (userStorage.getUserById(userId) == null) {
            log.error("Пользователь с Id =  {} не найден", userId);
            throw new NotFoundException("Пользователь с Id = " + userId + " не найден");
        }

        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().remove(userId);
    }

}

