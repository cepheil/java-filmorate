package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

/**
 * Класс {@code FilmService} содержит бизнес-логику, связанную с фильмами.
 *
 * <p>В текущей реализации:</p>
 * <ul>
 *     <li>Добавление лайка фильму от пользователя</li>
 *     <li>Удаление лайка у фильма от пользователя</li>
 * </ul>
 *
 * <p>Для работы использует:</p>
 * <ul>
 *     <li>{@link FilmStorage} — для получения и изменения данных о фильме</li>
 *     <li>{@link UserStorage} — для проверки существования пользователя</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    /**
     * Добавляет лайк фильму от указанного пользователя.
     *
     * @param filmId ID фильма, которому ставится лайк
     * @param userId ID пользователя, который ставит лайк
     * @throws NotFoundException если фильм или пользователь не найдены
     */
    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        if (film == null) {
            throw new NotFoundException("Фильм не найден.");
        }
        if (user == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        film.getLikes().add(userId);
    }

    /**
     * Удаляет лайк у фильма от указанного пользователя.
     *
     * @param filmId ID фильма, у которого удаляется лайк
     * @param userId ID пользователя, чей лайк удаляется
     * @throws NotFoundException если фильм или пользователь не найдены
     */
    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        if (film == null) {
            throw new NotFoundException("Фильм не найден.");
        }
        if (user == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        film.getLikes().remove(userId);
    }
}
