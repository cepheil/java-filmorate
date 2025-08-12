package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;


import java.util.Collection;
import java.util.Optional;

public interface FilmRepository {

    Film create(Film film);

    Film update(Film newFilm);

    Collection<Film> findAll();

    boolean delete(Long id);

    Optional<Film> findById(Long id);

    Collection<Film> getPopularFilms(int count);

    boolean existsById(long id);

}
