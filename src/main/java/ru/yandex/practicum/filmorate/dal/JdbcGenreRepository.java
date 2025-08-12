package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("jdbc")
public class JdbcGenreRepository extends BaseRepository<Genre> implements GenreRepository {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres ORDER BY genre_id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ? ORDER BY genre_id";


    public JdbcGenreRepository(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }


    @Override
    public Collection<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Genre> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }


    @Override
    public void loadGenresForFilms(Map<Long, Film> filmMap) {
        if (filmMap.isEmpty()) {
            return;
        }
        String sqlGenres = """
                    SELECT fg.film_id, g.genre_id, g.name
                    FROM film_genre AS fg
                    JOIN genres AS g ON fg.genre_id = g.genre_id
                    WHERE fg.film_id IN (%s)
                """.formatted(filmMap.keySet()
                .stream()
                .map(id -> "?")
                .collect(Collectors.joining(",")));

        jdbc.query(sqlGenres, rs -> {
            Film film = filmMap.get(rs.getLong("film_id"));
            if (film.getGenres() == null) {
                film.setGenres(new ArrayList<>());
            }

            film.getGenres().add(new Genre(
                    rs.getLong("genre_id"),
                    rs.getString("name")
            ));
        }, filmMap.keySet().toArray());
    }


    @Override
    public boolean existsAllByIds(Collection<Long> ids) {
        if (ids.isEmpty()) return true;

        String sql = "SELECT COUNT(DISTINCT genre_id) FROM genres WHERE genre_id IN (%s)";
        String inClause = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        Integer count = jdbc.queryForObject(
                String.format(sql, inClause),
                Integer.class
        );
        return count != null && count == ids.size();
    }


}
