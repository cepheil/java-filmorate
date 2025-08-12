package ru.yandex.practicum.filmorate.dal;


import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface GenreRepository {

    Collection<Genre> findAll();

    Optional<Genre> findById(Long id);

    void loadGenresForFilms(Map<Long, Film> filmMap);

    boolean existsAllByIds(Collection<Long> ids);

}
