package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmRepository {

    Collection<Film> findAllFilms();

    Collection<Film> getPopularFilms(int count);

    Collection<Film> getPopularFilmsByGenreAndYear(int count, Long genreId, Integer year);

    Collection<Film> getPopularFilmsByGenre(int count, Long genreId);

    Collection<Film> getPopularFilmsByYear(int count, Integer year);

    Film createFilm(Film film);

    Film updateFilm(Film newFilm);

    Optional<Film> getFilmById(Long id);

    boolean deleteFilm(Long id);

    Collection<Film> findFilmsByDirectorSortedByYear(Long directorId);

    Collection<Film> findFilmsByDirectorSortedByLikes(Long directorId);

    Collection<Film> searchFilmsByTitle(String query);

    Collection<Film> searchFilmsByDirector(String query);

    Collection<Film> searchFilmsByTitleAndDirector(String query);

    Collection<Film> getRecommendedFilms(long userId);
}
