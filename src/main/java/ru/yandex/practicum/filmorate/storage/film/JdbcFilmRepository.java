package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.base.BaseRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("filmRepository")
public class JdbcFilmRepository extends BaseRepository<Film> implements FilmRepository {
    private static final String FIND_ALL_FILMS_QUERY = """
            SELECT f.*, m.name AS mpa_name
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            ORDER BY f.film_id
            """;

    private static final String FIND_FILM_BY_ID_QUERY = """
            SELECT f.*, m.name AS mpa_name
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            WHERE f.film_id = ?
            """;

    private static final String INSERT_FILM_QUERY = """
            INSERT INTO films (name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_FILM_QUERY = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
            WHERE film_id = ?
            """;

    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE film_id = ?";

    private static final String GET_POPULAR_FILM_QUERY = """
            SELECT f.*, m.name AS mpa_name,
            COUNT(l.user_id) AS like_count
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            LEFT JOIN likes l ON f.film_id = l.film_id
            ORDER BY like_count DESC
            LIMIT ?
            """;

    public JdbcFilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findAllFilms() {
        return findMany(FIND_ALL_FILMS_QUERY);
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        return findOne(FIND_FILM_BY_ID_QUERY, filmId);
    }

    @Override
    public Film createFilm(Film film) {
        long id = insert(
                INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        update(
                UPDATE_FILM_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId()
        );
        return newFilm;
    }

    @Override
    public boolean deleteFilm(Long filmId) {
        return delete(DELETE_FILM_QUERY, filmId);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return findMany(GET_POPULAR_FILM_QUERY, count);
    }
}
