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

/**
 * Реализация интерфейса {@link FilmStorage}, представляющая собой временное хранилище фильмов в памяти.
 *
 * <p>Этот класс предоставляет реализацию CRUD-операций над сущностью {@link Film} и хранит данные в
 * структуре {@link HashMap}, что делает его подходящим для временного использования или тестирования.</p>
 *
 * <h2>Основные функции:</h2>
 * <ul>
 *     <li>Добавление нового фильма</li>
 *     <li>Обновление существующего фильма</li>
 *     <li>Получение списка всех фильмов</li>
 *     <li>Получение фильма по ID</li>
 *     <li>Поиск самых популярных фильмов (по количеству лайков)</li>
 * </ul>
 *
 * <p>Класс также проверяет уникальность названия фильма и выбрасывает исключения:
 * {@link DuplicatedDataException} и {@link NotFoundException} при необходимости.</p>
 *
 * <p><b>Важно:</b> Данные, хранящиеся в этом хранилище, не сохраняются между запусками приложения.</p>
 *
 * @see FilmStorage
 * @see Film
 * @see DuplicatedDataException
 * @see NotFoundException
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    /**
     * Возвращает список всех фильмов, отсортированных по идентификатору.
     *
     * @return коллекция объектов типа {@link Film}
     */
    @Override
    public Collection<Film> findAllFilms() {
        log.info("GET /films - получение списка всех фильмов.");
        return films.values()
                .stream()
                .sorted(Comparator.comparing(Film::getId))
                .collect(Collectors.toList());
    }

    /**
     * Добавляет новый фильм в хранилище.
     *
     * <p>Перед добавлением проверяет, что фильм с таким названием ещё не существует.</p>
     *
     * @param film объект фильма, который необходимо добавить
     * @return объект {@link Film} с присвоенным ID
     * @throws DuplicatedDataException если фильм с таким названием уже существует
     */
    @Override
    public Film createFilm(Film film) {
        log.info("POST /films - попытка добавления фильма: {}", film.getName());
        if (films.values()
                .stream()
                .anyMatch(f -> f.getName().equalsIgnoreCase(film.getName()))) {
            log.error("Такое название уже существует: {}", film.getName());
            throw new DuplicatedDataException("Фильм с таким названием уже существует.");
        }
        film.setId(Long.valueOf(idCounter.getAndIncrement()));
        films.put(film.getId(), film);
        log.info("Фильм создан: ID={}", film.getId());
        return film;
    }

    /**
     * Обновляет данные существующего фильма.
     *
     * <p>Проверяет наличие фильма по ID и уникальность нового названия (если оно изменилось).</p>
     *
     * @param newFilm объект фильма с обновлёнными данными
     * @return обновлённый объект {@link Film}
     * @throws NotFoundException       если фильм с указанным ID не найден
     * @throws DuplicatedDataException если новое название уже занято
     */
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

    /**
     * Находит фильм по его идентификатору.
     *
     * @param id идентификатор фильма
     * @return объект {@link Film}, если найден, иначе null
     */
    @Override
    public Film getFilmById(Long id) {
        return films.get(id);
    }

    /**
     * Возвращает список самых популярных фильмов.
     *
     * <p>Фильмы сортируются по количеству лайков, в порядке убывания.</p>
     *
     * @param count максимальное количество возвращаемых фильмов
     * @return коллекция самых популярных фильмов
     */
    @Override
    public Collection<Film> getPopularFilms(int count) {
        return films.values()
                .stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
