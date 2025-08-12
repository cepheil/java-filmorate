package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


@Repository
@Qualifier("jdbc")
public class JdbcFilmRepository extends BaseRepository<Film> implements FilmRepository {


    private static final String INSERT_QUERY =
            "INSERT INTO films(name, description, release_date, duration, mpa_id)" +
                    "VALUES (?, ?, ?, ?, ?)";  //returning id
    private static final String UPDATE_QUERY =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
    private static final String FIND_ALL_QUERY =
            "SELECT f.*, rm.name AS mpa_name FROM  films AS f LEFT JOIN ratings_mpa AS rm ON f.mpa_id = rm.mpa_id";
    private static final String DELETE_QUERY =
            "DELETE FROM films WHERE id = ?";
    private static final String FIND_BY_FILM_ID_QUERY = """
            SELECT f.*, rm.name AS mpa_name
            FROM  films AS f
            LEFT JOIN ratings_mpa AS rm ON f.mpa_id = rm.mpa_id
            WHERE f.id = ?""";
    private static final String GET_POPULAR_FILM_QUERY = """
            SELECT f.*, rm.name AS mpa_name
            FROM films AS f
            LEFT JOIN likes AS l ON f.id = l.film_id
            JOIN ratings_mpa AS rm ON f.mpa_id = rm.mpa_id
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, rm.name
            ORDER BY COUNT(l.user_id) DESC
            LIMIT ?
            """;

    private static final String INSERT_GENRES_QUERY =
            "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_GENRES_QUERY =
            "DELETE FROM film_genre WHERE film_id = ?";


    public JdbcFilmRepository(JdbcTemplate jdbc, FilmRowMapper mapper) {
        super(jdbc, mapper);
    }


    @Override
    public Film create(Film film) {
        if (film.getDescription() == null) film.setDescription("");
        if (film.getGenres() == null) film.setGenres(new ArrayList<>());
        if (film.getLikes() == null) film.setLikes(new HashSet<>());

        Long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        // 2. Сохраняем жанры  add  list <Genre>
        saveGenres(film);

        return findById(id).orElseThrow(() ->
                new InternalServerException("Фильм не найден после создания")
        );
    }

    @Override
    public Film update(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        saveGenres(film);
        return findById(film.getId()).orElseThrow(() ->
                new InternalServerException("Фильм не найден после обновления")
        );
    }

    @Override
    public Collection<Film> findAll() {
        Collection<Film> films = findMany(FIND_ALL_QUERY);
        return films;
    }

    @Override
    public boolean delete(Long id) {
        return delete(DELETE_QUERY, id);
    }

    @Override
    public Optional<Film> findById(Long id) {
        Optional<Film> filmOptional = findOne(FIND_BY_FILM_ID_QUERY, id);
        return filmOptional;
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        Collection<Film> films = findMany(GET_POPULAR_FILM_QUERY, count);
        return films;
    }

    @Override
    public boolean existsById(long id) {
        String sql = "SELECT COUNT(*) FROM films WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    private void saveGenres(Film film) {
        jdbc.update(DELETE_GENRES_QUERY, film.getId()); // удаляем старые жанры
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Genre> uniqueGenres = film.getGenres()
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(
                            Genre::getId,
                            g -> g,
                            (g1, g2) -> g1,
                            LinkedHashMap::new
                    ))
                    .values().stream().collect(Collectors.toList());

            jdbc.batchUpdate(
                    INSERT_GENRES_QUERY,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, film.getId());
                            ps.setLong(2, uniqueGenres.get(i).getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return uniqueGenres.size();
                        }
                    }
            );
        }

    }

}
