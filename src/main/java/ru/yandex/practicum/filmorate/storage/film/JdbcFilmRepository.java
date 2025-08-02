package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.base.BaseRepository;
import ru.yandex.practicum.filmorate.storage.genre.GenreRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Qualifier("filmRepository")
public class JdbcFilmRepository extends BaseRepository<Film> implements FilmRepository {
    private static final String FIND_ALL_FILMS_QUERY = """
            SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            ORDER BY f.film_id
            """;

    private static final String FIND_FILM_BY_ID_QUERY = """
            SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description
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
            SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description,
            COUNT(l.user_id) AS like_count
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            LEFT JOIN likes l ON f.film_id = l.film_id
            GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, 
                 m.mpa_id, m.name, m.description
            ORDER BY like_count DESC
            LIMIT ?
            """;

    private static final String DELETE_GENRE_FILM_QUERY = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String INSERT_GENRE_FILM_QUERY = "INSERT INTO film_genre(film_id, genre_id) VALUES(?, ?)";

    private final GenreRepository genreRepository;

    public JdbcFilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper, GenreRepository genreRepository) {
        super(jdbc, mapper);
        this.genreRepository = genreRepository;
    }

    @Override
    public List<Film> findAllFilms() {
        return findMany(FIND_ALL_FILMS_QUERY).stream().peek(film -> {
            film.setGenres(genreRepository.findGenreByFilmId(film.getId()));
        }).collect(Collectors.toList());
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        return findOne(FIND_FILM_BY_ID_QUERY, filmId).map(film -> {
            film.setGenres(genreRepository.findGenreByFilmId(filmId));
            return film;
        });
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
        updateGenres(film.getGenres(), film.getId());
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
        updateGenres(newFilm.getGenres(), newFilm.getId());
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

    public void updateGenres(Set<Genre> genres, Long filmId) {
        boolean isDeleted = delete(DELETE_GENRE_FILM_QUERY, filmId);
        if (!genres.isEmpty()) {
            List<Genre> genresList = genres.stream().toList();
            jdbc.batchUpdate(INSERT_GENRE_FILM_QUERY, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, filmId);
                    ps.setInt(2, Math.toIntExact(genresList.get(i).getId()));
                }

                @Override
                public int getBatchSize() {
                    return genresList.size();
                }
            });
        }
    }
}
