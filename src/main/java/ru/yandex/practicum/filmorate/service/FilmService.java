package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.LikeRepository;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository jdbcFilmRepository;
    private final LikeRepository jdbcLikeRepository;
    private final EntityValidator entityValidator;


    public Film createFilm(Film film) {
        log.info("POST /films добавление фильма: {}", film.getName());
        entityValidator.validateFilm(film);
        entityValidator.validateFilmUniqueness(film);
        Film createdFilm = jdbcFilmRepository.create(film);
        log.info("Фильм: {} , ID: {} , добавлен!", createdFilm.getName(), createdFilm.getId());
        return createdFilm;
    }


    public Film updateFilm(Film newFilm) {
        log.info("PUT /films - обновление фильма: {}", newFilm.getName());
        entityValidator.validateFilmExists(newFilm.getId());
        entityValidator.validateFilm(newFilm);
        Film updatedFilm = jdbcFilmRepository.update(newFilm);
        log.info("Фильм с ID {} успешно обновлён", newFilm.getId());
        return updatedFilm;
    }


    public Collection<Film> getAllFilms() {
        log.info("GET /films - получение списка всех фильмов");
        return jdbcFilmRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Film::getId))
                .collect(Collectors.toList());
    }


    public Film getFilmById(Long id) {
        log.info("GET /films/{filmId} - получение фильма по его ID");
        if (id == null) {
            log.error("Запрос фильма с null-ID отклонён");
            throw new ValidationException("ID фильма не может быть null");
        }
        return jdbcFilmRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с ID=" + id + " не найден"));
    }


    public void deleteFilm(Long id) {
        log.info("DELETE /films/{filmId} - удаление фильма по его ID");
        if (id == null) {
            log.error("Удаление фильма с null-ID отклонён");
            throw new ValidationException("ID фильма не может быть null");
        }
        if (!jdbcFilmRepository.delete(id)) {
            log.warn("Фильм с ID={} не найден при попытке удаления", id);
            throw new NotFoundException("Фильм с ID=" + id + " не найден");
        }
        log.info("Фильм с ID {} успешно удалён", id);
    }


    public Collection<Film> getPopularFilms(int count) {
        log.info("//GET /films/popular?count={count}  Получение списка из {} популярных фильмов", count);
        if (count <= 0) {
            log.error("Запрос популярных фильмов с невалидным count={}", count);
            throw new ValidationException("Количество должно быть больше нуля. count =" + count);
        }
        return jdbcFilmRepository.getPopularFilms(count);
    }


    public void addLike(Long filmId, Long userId) {
        log.info("//PUT /films/{id}/like/{userId}  Добавление лайка фильму ID: {}, от пользователем ID: {}",
                filmId, userId);
        entityValidator.validateLikeOperation(filmId, userId);

        try {
            int rowsAffected = jdbcLikeRepository.addLike(filmId, userId);
            if (rowsAffected > 0) {
                log.info("Лайк фильму ID: {}, от пользователя ID: {} успешно поставлен", filmId, userId);
            } else {
                log.info("Лайк фильму ID: {} от пользователя ID: {} уже существует", filmId, userId);
            }
        } catch (DuplicatedDataException e) {
            log.info("Лайк уже существует: filmId={}, userId={}", filmId, userId);
        }

    }


    public void removeLike(Long filmId, Long userId) {
        log.info("//DELETE /films/{id}/like/{userId}  Удаление лайка у фильма ID: {}, от пользователя ID: {}",
                filmId, userId);
        entityValidator.validateLikeOperation(filmId, userId);
        int rowsAffected = jdbcLikeRepository.removeLike(filmId, userId);
        if (rowsAffected > 0) {
            log.info("Лайк удалён: filmId={}, userId={}", filmId, userId);
        } else {
            log.info("Лайк не найден: filmId={}, userId={}", filmId, userId);
            throw new NotFoundException("Лайк не найден");
        }
    }


}

