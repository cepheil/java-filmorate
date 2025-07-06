package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

/**
 * Интерфейс {@code FilmStorage} определяет контракт для реализации слоя хранения данных о фильмах.
 *
 * <p>Содержит методы для выполнения базовых операций сущности "Фильм", таких как:</p>
 * <ul>
 *     <li>Получение списка всех фильмов</li>
 *     <li>Поиск фильма по идентификатору</li>
 *     <li>Добавление нового фильма</li>
 *     <li>Обновление существующего фильма</li>
 *     <li>Получение самых популярных фильмов (по количеству лайков)</li>
 * </ul>
 *
 * <p>Интерфейс используется в рамках шаблона проектирования Repository и служит абстракцией,
 * которая позволяет легко менять реализацию хранилища (например, на in-memory или базу данных).</p>
 *
 * @see ru.yandex.practicum.filmorate.model.Film
 * @see ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage
 */
public interface FilmStorage {
    Collection<Film> findAllFilms();
    Collection<Film> getPopularFilms(int count);
    Film createFilm(Film film);
    Film updateFilm(Film newFilm);
    Film getFilmById(Long id);
}
