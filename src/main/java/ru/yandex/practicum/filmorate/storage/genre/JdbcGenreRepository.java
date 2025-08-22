package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.base.BaseNamedParameterRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class JdbcGenreRepository extends BaseNamedParameterRepository<Genre> implements GenreRepository {
    private static final String FIND_ALL_GENRES_QUERY = "SELECT * FROM genres";
    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = :genreId";
    private static final String FIND_FILM_GENRES_BY_ID_QUERY = """
            SELECT g.genre_id, g.name
            FROM film_genre fg
            JOIN genres g ON fg.genre_id = g.genre_id
            WHERE fg.film_id = :filmId
            ORDER BY g.genre_id
            """;

    public JdbcGenreRepository(NamedParameterJdbcOperations jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Genre> findGenreById(Long genreId) {
        Map<String, Object> params = new HashMap<>();
        params.put("genreId", genreId);
        return findOne(FIND_GENRE_BY_ID_QUERY, params);
    }

    @Override
    public List<Genre> findAllGenres() {
        return findMany(FIND_ALL_GENRES_QUERY, new HashMap<>());
    }

    @Override
    public Set<Genre> findGenreByFilmId(Long filmId) {
        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);
        return findMany(FIND_FILM_GENRES_BY_ID_QUERY, params).stream()
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(Genre::getId))));
    }

    @Override
    public void loadGenresForFilms(Map<Long, Film> filmMap) {
        if (filmMap.isEmpty()) {
            return;
        }

        // SQL-запрос для загрузки жанров для всех фильмов
        String sql = """
            SELECT fg.film_id, g.genre_id, g.name
            FROM film_genre fg
            JOIN genres g ON fg.genre_id = g.genre_id
            WHERE fg.film_id IN (:filmIds)
            """;

        // Подготовка параметров запроса
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("filmIds", filmMap.keySet());

        // Выполнение запроса и обработка результатов
        jdbc.query(sql, parameters, rs -> {
            Long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);

            // Если у фильма еще нет жанров, инициализируем пустое множество
            if (film.getGenres() == null) {
                film.setGenres(new HashSet<>());
            }

            // Добавляем жанр в фильм
            film.getGenres().add(new Genre(
                    rs.getLong("genre_id"),
                    rs.getString("name")
            ));
        });
    }
}

