package ru.yandex.practicum.filmorate.storage.Director;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.base.BaseNamedParameterRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("directorRepository")
public class JdbcDirectorRepository extends BaseNamedParameterRepository<Director> implements DirectorRepository {
    private static final String FIND_ALL_DIRECTORS_QUERY = """
            SELECT * FROM directors
            """;


    private static final String FIND_DIRECTOR_BY_ID_QUERY = """
            SELECT * FROM directors WHERE director_id = :directorId
            """;


    private static final String FIND_DIRECTORS_BY_FILM_ID_QUERY = """
            SELECT d.director_id, d.name
            FROM film_directors AS fd
            JOIN directors AS d ON fd.director_id = d.director_id
            WHERE fd.film_id = :filmId
            ORDER BY d.director_id
            """;


    private static final String INSERT_DIRECTOR_QUERY = """
            INSERT INTO directors (name)
            VALUES (:name)
            """;


    private static final String UPDATE_DIRECTOR_QUERY = """
            UPDATE directors
            SET name = :name
            WHERE director_id = :directorId
            """;


    private static final String DELETE_DIRECTOR_QUERY = """
            DELETE FROM directors
            WHERE director_id = :directorId
            """;


    public JdbcDirectorRepository(NamedParameterJdbcOperations jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Director> findAllDirectors() {
        return findMany(FIND_ALL_DIRECTORS_QUERY, new HashMap<>());
    }

    @Override
    public Optional<Director> findDirectorById(Long directorId) {
        Map<String, Object> params = new HashMap<>();
        params.put("directorId", directorId);
        return findOne(FIND_DIRECTOR_BY_ID_QUERY, params);
    }

    @Override
    public Set<Director> findDirectorByFilmId(Long filmId) {
        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);
        return findMany(FIND_DIRECTORS_BY_FILM_ID_QUERY, params)
                .stream()
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(Director::getId))));
    }

    @Override
    public Director createDirector(Director director) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", director.getName());

        long id = insert(INSERT_DIRECTOR_QUERY, params);
        director.setId(id);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", director.getName());
        params.put("directorId", director.getId());

        update(UPDATE_DIRECTOR_QUERY, params);
        return director;
    }

    @Override
    public boolean deleteDirector(Long directorId) {
        Map<String, Object> params = new HashMap<>();
        params.put("directorId", directorId);

        return delete(DELETE_DIRECTOR_QUERY, params);
    }

    @Override
    public void loadDirectorsForFilms(Map<Long, Film> filmMap) {
        if (filmMap.isEmpty()) {
            return;
        }
        String sql = """
                SELECT fd.film_id, d.director_id, d.name
                FROM film_directors AS fd
                JOIN directors AS d ON fd.director_id = d.director_id
                WHERE fd.film_id IN (:filmIds)
                """;

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("filmIds", filmMap.keySet());

        jdbc.query(sql, parameters, rs -> {
            Film film = filmMap.get(rs.getLong("film_id"));
            if (film.getDirectors() == null) {
                film.setDirectors(new HashSet<>());
            }

            film.getDirectors().add(
                    new Director(rs.getLong("director_id"), rs.getString("name")));
        });

    }


}
