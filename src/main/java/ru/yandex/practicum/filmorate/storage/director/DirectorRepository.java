package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DirectorRepository {

    List<Director> findAllDirectors();

    Optional<Director> findDirectorById(Long directorId);

    Set<Director> findDirectorByFilmId(Long filmId);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    boolean deleteDirector(Long id);

    void loadDirectorsForFilms(Map<Long, Film> filmMap);
}
