package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.base.BaseNamedParameterRepository;
import ru.yandex.practicum.filmorate.storage.genre.GenreRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("filmRepository")
public class JdbcFilmRepository extends BaseNamedParameterRepository<Film> implements FilmRepository {
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
            WHERE f.film_id = :filmId
            """;

    private static final String INSERT_FILM_QUERY = """
            INSERT INTO films (name, description, release_date, duration, mpa_id)
            VALUES (:name, :description, :releaseDate, :duration, :mpaId)
            """;

    private static final String UPDATE_FILM_QUERY = """
            UPDATE films
            SET name = :name, description = :description, release_date = :releaseDate, duration = :duration, mpa_id = :mpaId
            WHERE film_id = :filmId
            """;

    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE film_id = :filmId";

    private static final String GET_POPULAR_FILM_QUERY = """
            SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description,
            COUNT(l.user_id) AS like_count
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            LEFT JOIN likes l ON f.film_id = l.film_id
            GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id,
                 m.mpa_id, m.name, m.description
            ORDER BY like_count DESC
            LIMIT :count
            """;

    private static final String DELETE_GENRE_FILM_QUERY = "DELETE FROM film_genre WHERE film_id = :filmId";
    private static final String INSERT_GENRE_FILM_QUERY = """
            INSERT INTO film_genre(film_id, genre_id) VALUES(?, ?)""";

    private static final String FIND_FILMS_BY_DIRECTOR_BY_YEAR_QUERY = """
            SELECT f.*,
                   m.mpa_id AS mpa_id,
                   m.name AS mpa_name,
                   m.description AS mpa_description
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            JOIN film_directors fd ON f.film_id = fd.film_id
            WHERE fd.director_id = :directorId
            ORDER BY f.release_date ASC
            """;

    private static final String FIND_FILMS_BY_DIRECTOR_BY_LIKES_QUERY = """
            SELECT f.*,
                   m.mpa_id AS mpa_id,
                   m.name AS mpa_name,
                   m.description AS mpa_description,
                   COUNT(l.user_id) AS like_count
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            JOIN film_directors fd ON f.film_id = fd.film_id
            LEFT JOIN likes l ON f.film_id = l.film_id
            WHERE fd.director_id = :directorId
            GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id,
                     m.mpa_id, m.name, m.description
            ORDER BY like_count DESC
            """;

    private static final String DELETE_DIRECTOR_FILM_QUERY = "DELETE FROM film_directors WHERE film_id = :filmId";
    private static final String INSERT_FILM_DIRECTORS_QUERY = """
            INSERT INTO film_directors(film_id, director_id) VALUES(?, ?)""";

    private static final String SEARCH_FILMS_BY_TITLE_QUERY = """
            SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%'))
            ORDER BY f.film_id
            """;

    private static final String SEARCH_FILMS_BY_DIRECTOR_QUERY = """
            SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            JOIN film_directors fd ON f.film_id = fd.film_id
            JOIN directors d ON fd.director_id = d.director_id
            WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%'))
            ORDER BY f.film_id
            """;

    public JdbcFilmRepository(NamedParameterJdbcOperations jdbc, RowMapper<Film> mapper,
                              GenreRepository genreRepository) {
    private static final String SEARCH_FILMS_BY_TITLE_AND_DIRECTOR_QUERY = """
            SELECT DISTINCT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            LEFT JOIN film_directors fd ON f.film_id = fd.film_id
            LEFT JOIN directors d ON fd.director_id = d.director_id
            WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%'))
            ORDER BY f.film_id
            """;


    private static final String GET_POPULAR_FILMS_BY_GENRE_AND_YEAR_QUERY = """
            SELECT  f.*,
                    m.mpa_id AS mpa_id,
                    m.name AS mpa_name,
                    m.description AS mpa_description,
                    COUNT(DISTINCT l.user_id) AS like_count
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            LEFT JOIN likes l ON f.film_id = l.film_id
            JOIN film_genre fg ON f.film_id = fg.film_id
            WHERE fg.genre_id = :genreId
            AND EXTRACT(YEAR FROM f.release_date) = :year
            GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id,
                    m.mpa_id, m.name, m.description
            ORDER BY like_count DESC
            LIMIT :count
            """;

    private static final String GET_POPULAR_FILMS_BY_GENRE_QUERY = """
            SELECT f.*,
             	        m.mpa_id AS mpa_id,
            	        m.name AS mpa_name,
            	        m.description AS mpa_description,
            	        COUNT(DISTINCT l.user_id) AS like_count
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            LEFT JOIN likes l ON f.film_id = l.film_id
            JOIN film_genre fg ON f.film_id = fg.film_id AND fg.genre_id = :genreId
            GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id,
                    m.mpa_id, m.name, m.description
            ORDER BY like_count DESC
            LIMIT :count
            """;

    private static final String GET_POPULAR_FILMS_BY_YEAR_QUERY = """
            SELECT DISTINCT f.*,
                        m.mpa_id AS mpa_id,
                        m.name AS mpa_name,
                        m.description AS mpa_description,
                        COUNT(DISTINCT l.user_id) AS like_count
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
            LEFT JOIN likes l ON f.film_id = l.film_id
            WHERE EXTRACT(YEAR FROM f.release_date) = :year
            GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id,
                        m.mpa_id, m.name, m.description
            ORDER BY like_count DESC
            LIMIT :count
            """;

    private static final String GET_RECOMMENDED_FILMS_QUERY = """
            SELECT f.film_id film_id,
                   f.name name,
                   f.description description,
                   f.release_date  release_date,
                   f.duration duration,
                   r.mpa_id mpa_id,
                   r.name mpa_name,
                   r.description mpa_description
            FROM likes l
            LEFT JOIN films f ON l.film_id = f.film_id
            LEFT JOIN mpa_ratings r ON r.MPA_ID = f.MPA_ID
            WHERE l.user_id IN (
                         SELECT USER_id
                         FROM likes
                         WHERE FILM_ID IN (
                                           SELECT film_id
                                           FROM likes
                                           WHERE user_id = :userId
                                          )
                         AND user_id <> :userIdToo
                         GROUP BY user_id
                         ORDER BY count(film_id)
                         LIMIT 1)
            and l.film_id not in (
                                  SELECT film_id
                                  FROM likes
                                  WHERE user_id = :userIdThree
                                 )
            ORDER BY f.film_id
            """;

    private static final String GET_COMMON_FILMS_QUERY = """
            select f.film_id film_id,
                   f.name name,
                   f.description description,
                   f.release_date  release_date,
                   f.duration duration,
                   r.mpa_id mpa_id,
                   r.name mpa_name,
                   r.description mpa_description
            from films f
            JOIN likes l ON f.film_id = l.film_id
            JOIN mpa_ratings r ON f.mpa_id = r.mpa_id
            WHERE f.film_id IN (SELECT ul.film_id
                               FROM likes ul
                               JOIN likes fl ON ul.film_id = fl.film_id
                               WHERE ul.USER_id = :userId AND fl.user_id = :friendId)
            GROUP BY f.film_id
            ORDER BY COUNT(l.film_id) DESC
            """;

    public JdbcFilmRepository(NamedParameterJdbcOperations jdbc, RowMapper<Film> mapper) {
    public JdbcFilmRepository(NamedParameterJdbcOperations jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findAllFilms() {
        return findMany(FIND_ALL_FILMS_QUERY, new HashMap<>());
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);
        return findOne(FIND_FILM_BY_ID_QUERY, params);
    }

    @Override
    public Film createFilm(Film film) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", film.getName());
        params.put("description", film.getDescription());
        params.put("releaseDate", film.getReleaseDate());
        params.put("duration", film.getDuration());
        params.put("mpaId", film.getMpa().getId());

        long id = insert(INSERT_FILM_QUERY, params);
        film.setId(id);
        updateGenres(film.getGenres(), film.getId());
        updateDirector(film.getDirectors(), film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", newFilm.getName());
        params.put("description", newFilm.getDescription());
        params.put("releaseDate", newFilm.getReleaseDate());
        params.put("duration", newFilm.getDuration());
        params.put("mpaId", newFilm.getMpa().getId());
        params.put("filmId", newFilm.getId());

        update(UPDATE_FILM_QUERY, params);
        updateGenres(newFilm.getGenres(), newFilm.getId());
        updateDirector(newFilm.getDirectors(), newFilm.getId());
        return newFilm;
    }

    @Override
    public boolean deleteFilm(Long filmId) {
        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);
        return delete(DELETE_FILM_QUERY, params);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        Map<String, Object> params = new HashMap<>();
        params.put("count", count);
        return findMany(GET_POPULAR_FILM_QUERY, params);
    }

    @Override
    public Collection<Film> getPopularFilmsByGenreAndYear(int count, Long genreId, Integer year) {
        Map<String, Object> params = new HashMap<>();
        params.put("count", count);
        params.put("genreId", genreId);
        params.put("year", year);

        return findMany(GET_POPULAR_FILMS_BY_GENRE_AND_YEAR_QUERY, params);
    }

    @Override
    public Collection<Film> getPopularFilmsByGenre(int count, Long genreId) {
        Map<String, Object> params = new HashMap<>();
        params.put("count", count);
        params.put("genreId", genreId);


        return findMany(GET_POPULAR_FILMS_BY_GENRE_QUERY, params);
    }

    @Override
    public Collection<Film> getPopularFilmsByYear(int count, Integer year) {
        Map<String, Object> params = new HashMap<>();
        params.put("count", count);
        params.put("year", year);

        return findMany(GET_POPULAR_FILMS_BY_YEAR_QUERY, params);
    }


    @Override
    public Collection<Film> findFilmsByDirectorSortedByYear(Long directorId) {
        Map<String, Object> params = new HashMap<>();
        params.put("directorId", directorId);
        return findMany(FIND_FILMS_BY_DIRECTOR_BY_YEAR_QUERY, params);
    }


    @Override
    public Collection<Film> findFilmsByDirectorSortedByLikes(Long directorId) {
        Map<String, Object> params = new HashMap<>();
        params.put("directorId", directorId);
        return findMany(FIND_FILMS_BY_DIRECTOR_BY_LIKES_QUERY, params);
    }

    @Override
    public Collection<Film> searchFilmsByTitle(String query) {
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
        return findMany(SEARCH_FILMS_BY_TITLE_QUERY, params);
    }

    @Override
    public Collection<Film> searchFilmsByDirector(String query) {
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
        return findMany(SEARCH_FILMS_BY_DIRECTOR_QUERY, params);
    }

    @Override
    public Collection<Film> searchFilmsByTitleAndDirector(String query) {
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
        return findMany(SEARCH_FILMS_BY_TITLE_AND_DIRECTOR_QUERY, params);
    }

    public void updateGenres(Set<Genre> genres, Long filmId) {
        if (!genres.isEmpty()) {
            Map<String, Object> baseParams = new HashMap<>();
            baseParams.put("filmId", filmId);

            jdbc.update(DELETE_GENRE_FILM_QUERY, baseParams);

            List<Genre> genreList = new ArrayList<>(genres);

            jdbc.getJdbcOperations().batchUpdate(
                    INSERT_GENRE_FILM_QUERY,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, filmId);
                            ps.setInt(2, Math.toIntExact(genreList.get(i).getId()));
                        }

                        @Override
                        public int getBatchSize() {
                            return genreList.size();
                        }
                    }
            );
        }
    }

    public void updateDirector(Set<Director> directors, Long filmId) {
        if (!directors.isEmpty()) {
            Map<String, Object> baseParams = new HashMap<>();
            baseParams.put("filmId", filmId);

            jdbc.update(DELETE_DIRECTOR_FILM_QUERY, baseParams);

            List<Director> directorsList = new ArrayList<>(directors);

            jdbc.getJdbcOperations().batchUpdate(
                    INSERT_FILM_DIRECTORS_QUERY,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, filmId);
                            ps.setInt(2, Math.toIntExact(directorsList.get(i).getId()));
                        }

                        @Override
                        public int getBatchSize() {
                            return directorsList.size();
                        }
                    }
            );
        }
    }

    @Override
    public Collection<Film> getRecommendedFilms(long userId) {
        Map<String, Long> parameters = new HashMap<>();
        parameters.put("userId", userId);
        parameters.put("userIdToo", userId);
        parameters.put("userIdThree", userId);
        return findMany(GET_RECOMMENDED_FILMS_QUERY, parameters);
    }

    @Override
    public Collection<Film> getCommonFilms(long userId, long friendId) {
        Map<String, Long> parameters = new HashMap<>();
        parameters.put("userId", userId);
        parameters.put("friendId", friendId);
        return findMany(GET_COMMON_FILMS_QUERY, parameters)
                .stream()
                .peek(film -> film.setGenres(genreRepository.findGenreByFilmId(film.getId())))
                .collect(Collectors.toList());
    }

}
