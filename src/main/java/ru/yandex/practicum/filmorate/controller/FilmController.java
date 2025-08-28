package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    /**
     * Получить список всех фильмов.
     *
     * @return коллекция всех фильмов
     */
    @GetMapping
    public Collection<Film> findAllFilms() {
        log.info("Получение всех фильмов");
        return filmService.findAllFilms();
    }

    /**
     * Получить список популярных фильмов.
     *
     * @param count   количество фильмов для возврата (по умолчанию 10)
     * @param genreId идентификатор жанра (опционально)
     * @param year    год выпуска фильма (опционально)
     * @return коллекция популярных фильмов
     */
    @GetMapping("/popular")
    public Collection<Film> getPopulateFilms(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer year
    ) {
        log.info("Получен запрос на получение популярных фильмов: count={}, genreId={}, year={}", count, genreId, year);
        return filmService.getPopularFilms(count, genreId, year);
    }

    /**
     * Получить фильм по ID.
     *
     * @param id идентификатор фильма
     * @return фильм с указанным ID
     * NotFoundException если фильм не найден
     */
    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Получен запрос на получение фильма с ID: {}", id);
        return filmService.getFilmById(id);
    }

    /**
     * Создать новый фильм.
     *
     * @param film объект фильма для создания
     * @return созданный фильм с присвоенным ID
     */
    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма: {}", film.getName());
        return filmService.createFilm(film);
    }

    /**
     * Обновить существующий фильм.
     *
     * @param newFilm объект фильма с обновлёнными данными
     * @return обновлённый фильм
     */
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        log.info("Получен запрос на обновление фильма с ID: {}", newFilm.getId());
        return filmService.updateFilm(newFilm);
    }

    /**
     * Добавить лайк фильму.
     *
     * @param filmId идентификатор фильма
     * @param userId идентификатор пользователя, который ставит лайк
     */
    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable Long filmId,
                        @PathVariable Long userId) {
        log.info("Получен запрос на добавление лайка фильму с ID {} от пользователя с ID {}", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    /**
     * Удалить лайк фильма.
     *
     * @param filmId идентификатор фильма
     * @param userId идентификатор пользователя, который удаляет лайк
     */
    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable Long filmId,
                           @PathVariable Long userId) {
        log.info("Получен запрос на удаление лайка фильма с ID {} от пользователя с ID {}", filmId, userId);
        filmService.removeLike(filmId, userId);
    }

    /**
     * Получить фильмы режиссёра, отсортированные по году или количеству лайков.
     *
     * @param directorId идентификатор режиссёра
     * @param sortBy     параметр сортировки (year или likes)
     * @return коллекция фильмов режиссёра
     */
    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsByDirector(@PathVariable Long directorId,
                                               @RequestParam String sortBy) {
        log.info("Получен запрос на получение фильмов режиссёра с ID {}, отсортированных по {}", directorId, sortBy);
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    /**
     * Поиск фильмов по названию или режиссёру.
     *
     * @param query строка для поиска
     * @param by    параметр поиска (title или director)
     * @return коллекция найденных фильмов
     */
    @GetMapping("/search")
    public Collection<Film> searchFilm(@RequestParam String query,
                                       @RequestParam(defaultValue = "title") String by) {
        log.info("Получен запрос на поиск фильмов: query={}, by={}", query, by);
        return filmService.searchFilms(query, by);
    }

    /**
     * Получить общие фильмы двух пользователей.
     *
     * @param userId   идентификатор первого пользователя
     * @param friendId идентификатор второго пользователя
     * @return коллекция общих фильмов
     */
    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam("userId") Long userId,
                                           @RequestParam("friendId") Long friendId) {
        log.info("Получен запрос на получение общих фильмов пользователей с ID {} и {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    /**
     * Удалить фильм по ID.
     *
     * @param filmId идентификатор фильма для удаления
     */
    @DeleteMapping("/{filmId}")
    public void removeFilm(@PathVariable Long filmId) {
        log.info("Получен запрос на удаление фильма с ID: {}", filmId);
                filmService.removeFilm(filmId);
    }
}
