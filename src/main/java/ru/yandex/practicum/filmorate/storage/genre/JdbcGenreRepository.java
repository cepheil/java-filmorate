package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.base.BaseRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class JdbcGenreRepository extends BaseRepository<Genre> implements GenreRepository {
    private static final String FIND_ALL_GENRES_QUERY = "SELECT * FROM genres";
    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ?";
    private static final String FIND_FILM_GENRES_BY_ID_QUERY = """
            SELECT g.genre_id, g.genre_name
            FROM films f
            JOIN film_genre fg ON f.film_id = fg.film_id
            JOIN genres g ON fg.genre_id = g.genre_id
            WHERE f.film_id = ?
            """;

    public JdbcGenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Genre> findGenreById(Long genreId) {
        return findOne(FIND_GENRE_BY_ID_QUERY, genreId);
    }

    @Override
    public List<Genre> findAllGenres() {
        return findMany(FIND_ALL_GENRES_QUERY);
    }

    @Override
    public Set<Genre> findGenreByFilmId(Long filmId) {
        return new HashSet<>(findMany(FIND_FILM_GENRES_BY_ID_QUERY, filmId));
    }
}
