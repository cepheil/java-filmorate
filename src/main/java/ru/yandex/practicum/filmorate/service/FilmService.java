package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmRepository;
import ru.yandex.practicum.filmorate.storage.like.LikeRepository;
import ru.yandex.practicum.filmorate.storage.user.UserRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final ValidationService validationService;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public Collection<Film> findAllFilms() {
        log.debug("Попытка получения всех фильмов");
        return filmRepository.findAllFilms();
    }

    public Film getFilmById(Long filmId) {
        log.debug("Попытка получения фильма по ID: {}", filmId);
        validationService.validateFilmExists(filmId);
        return filmRepository.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));
    }

    public Film createFilm(Film film) {
        log.debug("Попытка создания фильма: {}", film.getName());
        validationService.validateFilm(film);
        Film createdFilm = filmRepository.createFilm(film);
        log.info("Создан фильм с ID: {}", createdFilm.getId());
        return createdFilm;
    }

    public Film updateFilm(Film newFilm) {
        log.debug("Попытка обновления фильма с ID: {}", newFilm.getId());
        validationService.validateFilm(newFilm);
        Film updatedFilm = filmRepository.updateFilm(newFilm);
        log.info("Фильм с ID {} обновлен", newFilm.getId());
        return updatedFilm;
    }

    public Collection<Film> getPopularFilms(int count) {
        log.debug("Попытка получения популярных фильмов в количестве {} штук", count);
        if (count <= 0) {
            throw new ValidationException("Количество фильмов должно быть положительным числом.");
        }
        return filmRepository.getPopularFilms(count);
    }

    public void addLike(Long filmId, Long userId) {
        log.debug("Попытка добавления лайка фильму {} от пользователя {}", filmId, userId);
        validationService.validateFilmAndUserIds(filmId, userId);
        validationService.validateFilmExists(filmId);
        validationService.validateUserExists(userId);
        likeRepository.addLike(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.debug("Попытка удаления лайка у фильма {} от пользователя {}", filmId, userId);
        validationService.validateFilmAndUserIds(filmId, userId);
        likeRepository.removeLike(filmId, userId);
        log.info("Пользователь {} убрал лайк у фильма {}", userId, filmId);
    }
}
