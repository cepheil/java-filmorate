package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Director.DirectorRepository;
import ru.yandex.practicum.filmorate.storage.film.FilmRepository;
import ru.yandex.practicum.filmorate.storage.genre.GenreRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final ValidationService validationService;
    private final FilmRepository filmRepository;
    private final LikeService likeService;
    private final DirectorRepository directorRepository;
    private final GenreRepository genreRepository;

    public Collection<Film> findAllFilms() {
        log.info("Попытка получения всех фильмов");

        List<Film> allFilms = new ArrayList<>(filmRepository.findAllFilms());
        if (allFilms.isEmpty()) {
            log.info("GET /films. Получена пустая коллекция");
            return allFilms;
        }
        loadAdditionalData(allFilms);
        log.info("по запросу GET /films получена коллекция из  {} фильмов", allFilms.size());
        return allFilms;
    }

    public Film getFilmById(Long filmId) {
        log.info("Попытка получения фильма по ID: {}", filmId);
        validationService.validateFilmExists(filmId);
        Film film = filmRepository.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));
        loadAdditionalData(List.of(film));
        log.info("GET /films/{filmId} - получен  фильм ID={}, name={}", filmId, film.getName());
        return film;
    }

    public Film createFilm(Film film) {
        log.info("Попытка создания фильма: {}", film.getName());
        validationService.validateFilm(film);
        Film createdFilm = filmRepository.createFilm(film);
        log.info("Создан фильм с ID: {}", createdFilm.getId());
        return createdFilm;
    }

    public Film updateFilm(Film newFilm) {
        log.info("Попытка обновления фильма с ID: {}", newFilm.getId());
        validationService.validateFilm(newFilm);

        Film updatedFilm = filmRepository.updateFilm(newFilm);
        log.info("Фильм с ID {} обновлен", newFilm.getId());
        return updatedFilm;
    }

    public Collection<Film> getPopularFilms(int count, Long genreId, Integer year) {
        log.info("Попытка получения популярных фильмов: count={}, genreId={}, year={}", count, genreId, year);

        if (count <= 0) {
            throw new ValidationException("Количество фильмов должно быть положительным числом.");
        }

        List<Film> popularFilms;
        if (genreId != null && year != null) {
            popularFilms = new ArrayList<>(filmRepository.getPopularFilmsByGenreAndYear(count, genreId, year));
        } else if (genreId != null) {
            popularFilms = new ArrayList<>(filmRepository.getPopularFilmsByGenre(count, genreId));
        } else if (year != null) {
            popularFilms = new ArrayList<>(filmRepository.getPopularFilmsByYear(count, year));
        } else {
            popularFilms = new ArrayList<>(filmRepository.getPopularFilms(count));
        }

        if (popularFilms.isEmpty()) {
            log.info("GET /films/popular?count={}. Получена пустая коллекция", count);
            return popularFilms;
        }

        loadAdditionalData(popularFilms);

        log.info("по запросу GET /films/popular?count={}&genreId={}&year={} " +
                 "получена коллекция из {} популярных фильмов",
                count, genreId, year, popularFilms.size());

        return popularFilms;
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Попытка добавления лайка фильму {} от пользователя {}", filmId, userId);
        validationService.validateFilmAndUserIds(filmId, userId);
        validationService.validateFilmExists(filmId);
        validationService.validateUserExists(userId);
        likeService.addLike(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Попытка удаления лайка у фильма {} от пользователя {}", filmId, userId);
        validationService.validateFilmAndUserIds(filmId, userId);
        likeService.removeLike(filmId, userId);
        log.info("Пользователь {} убрал лайк у фильма {}", userId, filmId);
    }

    //GET /films/director/{directorId}?sortBy=[year,likes]
    public Collection<Film> getFilmsByDirector(Long directorId, String sortBy) {
        log.info("Попытка получить список фильмов по режиссеру сортированный по {}", sortBy);
        validationService.validateDirectorExists(directorId);

        List<Film> films = switch (sortBy) {
            case "year" -> new ArrayList<>(filmRepository.findFilmsByDirectorSortedByYear(directorId));
            case "likes" -> new ArrayList<>(filmRepository.findFilmsByDirectorSortedByLikes(directorId));
            default -> throw new ValidationException("Неверный параметр sortBy: " + sortBy);
        };

        if (films.isEmpty()) {
            log.info("GET /films/director/{}. Получена пустая коллекция", directorId);
            return films;
        }
        loadAdditionalData(films);
        log.info("по запросу GET /films/director/{} получена коллекция из {} фильмов", directorId, films.size());
        return films;
    }

    public Collection<Film> searchFilms(String query, String by) {
        log.info("Поиск фильмов с query: {} и by: {}", query, by);
        validationService.validateSearchQuery(query);
        Set<String> searchBy = validationService.validateAndParseSearchBy(by);
        Collection<Film> films;
        if (searchBy.contains("title") && searchBy.contains("director")) {
            films = filmRepository.searchFilmsByTitleAndDirector(query);
        } else if (searchBy.contains("title")) {
            films = filmRepository.searchFilmsByTitle(query);
        } else if (searchBy.contains("director")) {
            films = filmRepository.searchFilmsByDirector(query);
        } else {
            throw new ValidationException("Неверный параметр 'by'. Используйте 'title', 'director' или оба.");
        }
        loadAdditionalData(new ArrayList<>(films));
        return films;
    }


    private void loadAdditionalData(List<Film> films) {
        if (films == null || films.isEmpty()) {
            return;
        }

        Map<Long, Film> filmMap = films
                .stream()
                .collect(Collectors.toMap(Film::getId, f -> f)); // формируем мапу
        //Заглушки для аналогичных методов по добавлению жанров и лайков.
        genreRepository.loadGenresForFilms(filmMap);
        //likeRepository.loadLikesForFilms(filmMap);
        directorRepository.loadDirectorsForFilms(filmMap);
    }


}
