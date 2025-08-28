package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.director.DirectorRepository;
import ru.yandex.practicum.filmorate.storage.film.FilmRepository;
import ru.yandex.practicum.filmorate.storage.genre.GenreRepository;
import ru.yandex.practicum.filmorate.storage.review.ReviewRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
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
    private final UserService userService;
    private final ReviewRepository reviewRepository;

    public Collection<Film> findAllFilms() {
        log.info("Попытка получения всех фильмов");
        Collection<Film> films = filmRepository.findAllFilms();
        if (films.isEmpty()) {
            log.info("GET /films. Получена пустая коллекция");
            return films;
        }
        loadAdditionalData(new ArrayList<>(films));
        for (Film film : films) {
            List<Review> reviews = reviewRepository.getReviewsByFilmId(film.getId(), Integer.MAX_VALUE);
            film.setReviews(reviews);
        }
        log.info("по запросу GET /films получена коллекция из {} фильмов", films.size());
        return films;
    }

    public Film getFilmById(Long filmId) {
        log.info("Попытка получения фильма по ID: {}", filmId);
        validationService.validateFilmExists(filmId);
        Film film = filmRepository.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));
        Set<Genre> genres = genreRepository.findGenreByFilmId(filmId);
        film.setGenres(genres);
        List<Review> reviews = reviewRepository.getReviewsByFilmId(filmId, Integer.MAX_VALUE);
        film.setReviews(reviews);
        loadAdditionalData(List.of(film));
        log.info("GET /films/{filmId} - получен  фильм ID={}, name={}", filmId, film.getName());
        return film;
    }

    public Film createFilm(Film film) {
        log.info("Попытка создания фильма: {}", film.getName());
        validationService.validateFilm(film);
        Film createdFilm = filmRepository.createFilm(film);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            film.setGenres(film.getGenres()
                    .stream()
                    .sorted(Comparator.comparingLong(Genre::getId))
                    .collect(Collectors.toCollection(TreeSet::new)));
        }
        log.info("Создан фильм с ID: {}", createdFilm.getId());
        return createdFilm;
    }

    public Film updateFilm(Film newFilm) {
        log.info("Попытка обновления фильма с ID: {}", newFilm.getId());
        validationService.validateFilmExists(newFilm.getId()); //проверка на обновление фильма с несуществующим id
        validationService.validateFilm(newFilm);
        //костыль на сортировку жанров в фильме
        List<Genre> test = new ArrayList<>(newFilm.getGenres());
        test.sort(Comparator.comparingLong(Genre::getId));
        newFilm.setGenres(new TreeSet<>(test));

        Film updatedFilm = filmRepository.updateFilm(newFilm);
        if (newFilm.getGenres() == null || newFilm.getGenres().isEmpty()) { //обновление фильма (новый фильм пришел без жанра)
            genreRepository.deleteFilmGenresByFilmId(newFilm.getId());
        }
        if (newFilm.getDirectors() == null || newFilm.getDirectors().isEmpty()) { //обновление фильма (новый фильм пришел без режиссера)
            directorRepository.deleteAllFilmDirectors(newFilm.getId());
        }
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

    public void removeFilm(Long filmId) {
        log.info("Попытка удаления фильма {} ", filmId);
        validationService.validateFilmExists(filmId);
        filmRepository.deleteFilm(filmId);
        genreRepository.deleteFilmGenresByFilmId(filmId);
        likeService.removeLikesByFilmId(filmId);
        log.info("Фильм {}, а также связанные с ним лайки и жанры удалены", filmId);
    }

    private void loadAdditionalData(List<Film> films) {
        if (films == null || films.isEmpty()) {
            return;
        }

        Map<Long, Film> filmMap = films
                .stream()
                .collect(Collectors.toMap(Film::getId, f -> f));
        genreRepository.loadGenresForFilms(filmMap);
        directorRepository.loadDirectorsForFilms(filmMap);
    }

    public Collection<Film> getCommonFilms(long userId, long friendId) {
        Collection<Film> filmList = filmRepository.getCommonFilms(userId, friendId);
        loadAdditionalData(new ArrayList<>(filmList));
        log.info("Отгрузил {} общих фильмов для пользователей {} и {}", filmList.size(),
                userId, friendId);
        return filmList;
    }
}
