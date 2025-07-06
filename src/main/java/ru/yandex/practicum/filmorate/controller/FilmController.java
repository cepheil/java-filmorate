package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

/**
 * Контроллер {@code FilmController} обрабатывает HTTP-запросы, связанные с фильмами.
 *
 * <p>Поддерживает следующие операции:</p>
 * <ul>
 *     <li>Получение списка всех фильмов</li>
 *     <li>Добавление нового фильма</li>
 *     <li>Обновление существующего фильма</li>
 *     <li>Получение самых популярных фильмов</li>
 *     <li>Добавление и удаление лайков у фильма</li>
 * </ul>
 *
 * <p>Все операции выполняются через соответствующие слои: хранение данных ({@link FilmStorage}) и бизнес-логику ({@link FilmService}).</p>
 */
@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    /**
     * Возвращает список всех доступных фильмов.
     *
     * @return коллекция объектов типа {@link Film}
     */
    @GetMapping
    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    /**
     * Возвращает список самых популярных фильмов.
     * Популярность определяется количеством лайков.
     *
     * @param count количество возвращаемых фильмов (по умолчанию 10)
     * @return коллекция объектов типа {@link Film}
     */
    @GetMapping("/popular")
    public Collection<Film> getPopulateFilms(@RequestParam(defaultValue = "10") int count) {
        return filmStorage.getPopularFilms(count);
    }

    /**
     * Добавляет новый фильм в систему.
     *
     * @param film объект фильма, переданный в теле запроса
     * @return добавленный объект типа {@link Film}
     */
    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmStorage.createFilm(film);
    }

    /**
     * Обновляет данные существующего фильма.
     *
     * @param newFilm объект фильма с новыми данными
     * @return обновлённый объект типа {@link Film}
     */
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    /**
     * Добавляет лайк фильму от указанного пользователя.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя, который ставит лайк
     */
    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable Long filmId,
                        @PathVariable Long userId) {
        filmService.addLike(filmId, userId);
    }

    /**
     * Удаляет лайк у фильма от указанного пользователя.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя, отзывающего лайк
     */
    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable Long filmId,
                           @PathVariable Long userId) {
        filmService.removeLike(filmId, userId);
    }
}
