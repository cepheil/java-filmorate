package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorRepository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmRepository;
import ru.yandex.practicum.filmorate.storage.review.ReviewRepository;
import ru.yandex.practicum.filmorate.storage.user.UserRepository;
import ru.yandex.practicum.filmorate.storage.genre.GenreRepository;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRepository;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValidationService {
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;
    private final ReviewRepository reviewRepository;
    private final DirectorRepository directorRepository;

    public void validateUserExists(Long userId) {
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    public void validateUsersExist(Long userId1, Long userId2) {
        if (userId1 == null || userId2 == null) {
            throw new ValidationException("ID пользователей не могут быть null");
        }
        validateUserExists(userId1);
        validateUserExists(userId2);
    }

    public void validateFilmAndUserIds(Long filmId, Long userId) {
        if (filmId == null) {
            throw new ValidationException("ID фильма не может быть null");
        }
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
    }

    public void validateFilmExists(Long filmId) {
        if (filmId == null) {
            throw new ValidationException("ID фильма не может быть null");
        }
        filmRepository.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));
    }

    // обращение к БД в цикле!
    public void validateFilm(Film film) {
        if (film == null) {
            throw new ValidationException("Фильм не может быть null.");
        }
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new ValidationException("Фильм должен иметь рейтинг MPA");
        }
        validateMpaExists(film.getMpa().getId());
        if (film.getGenres() != null) {
            for (var genre : film.getGenres()) {
                if (genre.getId() == null) {
                    throw new ValidationException("Жанр должен иметь ID.");
                }
                validateGenreExists(genre.getId());
            }
        }
    }

    public void validateGenreExists(Long genreId) {
        if (genreId == null) {
            throw new ValidationException("ID жанра не может быть null");
        }
        genreRepository.findGenreById(genreId)
                .orElseThrow(() -> new NotFoundException("Жанр с ID " + genreId + " не найден"));
    }

    public void validateMpaExists(Long mpaId) {
        if (mpaId == null) {
            throw new ValidationException("ID рейтинга не может быть null");
        }
        mpaRepository.findMpaById(mpaId)
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA с ID " + mpaId + " не найден"));
    }

    public void validateDirectorExists(Long directorId) {
        if (directorId == null) {
            throw new ValidationException("ID режиссера не может быть null");
        }
        directorRepository.findDirectorById(directorId)
                .orElseThrow(() -> new NotFoundException("Режиссер с ID " + directorId + " не найден"));
    }

    public void validateSearchQuery(String query) {
        if (query == null || query.isEmpty()) {
            throw new ValidationException("Текст для поиска не может быть пустым.");
        }
    }

    public Set<String> validateAndParseSearchBy(String by) {
        if (by == null || by.isEmpty()) {
            throw new ValidationException("Текст для поиска не может быть пустым.");
        }
        Set<String> validOptions = Set.of("title", "director");
        Set<String> searchBy = Arrays.stream(by.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
        if (!validOptions.containsAll(searchBy)) {
            throw new ValidationException("Неверный параметр. Используйте 'title', 'director' или оба.");
        }
        return searchBy;
    }

    public void validateReviewExists(Long reviewId) {
        if (reviewId == null) {
            throw new ValidationException("ID отзыва не может быть null");
        }
        reviewRepository.getReviewById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с ID " + reviewId + " не найден"));
    }

    public void validateReview(Review review) {
        if (review == null) {
            throw new ValidationException("Отзыв не может быть null");
        }
        validateReviewExists(review.getUserId());
        validateReviewExists(review.getFilmId());
    }
}