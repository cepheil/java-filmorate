package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для проверки работы контроллера {@link FilmController}.
 *
 * <p>Тесты покрывают основные методы контроллера:</p>
 * <ul>
 *     <li>Получение списка всех фильмов</li>
 *     <li>Получение популярных фильмов</li>
 *     <li>Создание фильма</li>
 *     <li>Обновление фильма</li>
 *     <li>Добавление и удаление лайков</li>
 * </ul>
 *
 * <p>Для тестирования используются моки ({@link Mock}) объектов {@link FilmStorage} и {@link FilmService},
 * а тестируемый класс ({@link FilmController}) внедряется через {@link InjectMocks}.</p>
 */
public class FilmControllerTest {

    @Mock
    private FilmStorage filmStorage;

    @Mock
    private FilmService filmService;

    @InjectMocks
    private FilmController filmController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Проверяет, что метод {@link FilmController#findAllFilms()} возвращает список всех фильмов.
     *
     * <p>Ожидается, что вызов делегируется в {@link FilmStorage#findAllFilms()}</p>
     */
    @Test
    public void testFindAllFilms() {
        Film film1 = new Film();
        Film film2 = new Film();
        Collection<Film> films = Arrays.asList(film1, film2);

        when(filmStorage.findAllFilms()).thenReturn(films);

        Collection<Film> result = filmController.findAllFilms();

        assertEquals(2, result.size());
        verify(filmStorage, times(1)).findAllFilms();
    }

    /**
     * Проверяет, что метод {@link FilmController#getPopulateFilms(int)} возвращает указанное количество фильмов.
     *
     * <p>Ожидается, что вызов делегируется в {@link FilmStorage#getPopularFilms(int)}</p>
     */
    @Test
    public void testGetPopulateFilms() {
        int count = 10;
        Film film1 = new Film();
        Film film2 = new Film();
        Collection<Film> popularFilms = Arrays.asList(film1, film2);

        when(filmStorage.getPopularFilms(count)).thenReturn(popularFilms);

        Collection<Film> result = filmController.getPopulateFilms(count);

        assertEquals(2, result.size());
        verify(filmStorage, times(1)).getPopularFilms(count);
    }

    /**
     * Проверяет, что метод {@link FilmController#createFilm(Film)} корректно создаёт новый фильм.
     *
     * <p>Ожидается, что вызов делегируется в {@link FilmStorage#createFilm(Film)}</p>
     */
    @Test
    public void testCreateFilm() {
        Film film = new Film();

        when(filmStorage.createFilm(film)).thenReturn(film);

        Film result = filmController.createFilm(film);

        assertEquals(film, result);
        verify(filmStorage, times(1)).createFilm(film);
    }

    /**
     * Проверяет, что метод {@link FilmController#updateFilm(Film)} корректно обновляет данные фильма.
     *
     * <p>Ожидается, что вызов делегируется в {@link FilmStorage#updateFilm(Film)}</p>
     */
    @Test
    public void testUpdateFilm() {
        Film newFilm = new Film();

        when(filmStorage.updateFilm(newFilm)).thenReturn(newFilm);

        Film result = filmController.updateFilm(newFilm);

        assertEquals(newFilm, result);
        verify(filmStorage, times(1)).updateFilm(newFilm);
    }

    /**
     * Проверяет, что метод {@link FilmController#addLike(Long, Long)} добавляет лайк фильму от пользователя.
     *
     * <p>Ожидается, что вызов делегируется в {@link FilmService#addLike(Long, Long)}</p>
     */
    @Test
    public void testAddLike() {
        Long filmId = (Long) 1L;
        Long userId = (Long) 2L;

        filmController.addLike(filmId, userId);

        verify(filmService, times(1)).addLike(filmId, userId);
    }

    /**
     * Проверяет, что метод {@link FilmController#removeLike(Long, Long)} удаляет лайк у фильма от пользователя.
     *
     * <p>Ожидается, что вызов делегируется в {@link FilmService#removeLike(Long, Long)}</p>
     */
    @Test
    public void testRemoveLike() {
        Long filmId = (Long) 1L;
        Long userId = (Long) 2L;

        filmController.removeLike(filmId, userId);

        verify(filmService, times(1)).removeLike(filmId, userId);
    }
}
