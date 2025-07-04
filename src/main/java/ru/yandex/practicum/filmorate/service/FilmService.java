package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public void addLike(Long filmId, Long userId) {
        filmStorage.addLike(filmId, userId);
        log.info("Пользователь: ID{} лайкнул фильм: ID {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        filmStorage.removeLike(filmId, userId);
        log.info("Пользователь: ID {} удалил лайк фильму: ID {}", userId, filmId);
    }

    public Collection<Film> getPopularFilms(int count) {
        Collection<Film> popularFilms = filmStorage.getPopularFilms(count);
        log.debug("Запрошено {} популярных фильмов", count);
        return popularFilms;
    }
}
