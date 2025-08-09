package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


@Repository
@Qualifier("jdbc")
public class JdbcFilmRepository extends BaseRepository<Film> implements FilmRepository {
    private final JdbcGenreRepository genreRepository;
    private final JdbcLikeRepository likeRepository;
    private final JdbcRatingMpaRepository ratingRepository;


    private static final String INSERT_QUERY =
            "INSERT INTO films(name, description, release_date, duration, mpa_id)" +
                    "VALUES (?, ?, ?, ?, ?)";  //returning id
    private static final String UPDATE_QUERY =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
    private static final String FIND_ALL_QUERY =
            "SELECT f.*, rm.name AS mpa_name FROM  films AS f INNER JOIN ratings_mpa AS rm ON f.mpa_id = rm.mpa_id";
    private static final String DELETE_QUERY =
            "DELETE FROM films WHERE id = ?";
    private static final String FIND_BY_FILM_ID_QUERY = """
            SELECT f.*, rm.name AS mpa_name
            FROM  films AS f
            INNER JOIN ratings_mpa AS rm ON f.mpa_id = rm.mpa_id
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
    private static final String FIND_GENRES_BY_FILM_QUERY =
            "SELECT g.genre_id, g.name FROM film_genre AS fg JOIN genres AS g ON fg.genre_id = g.genre_id WHERE fg.film_id = ?";
    private static final String FIND_LIKES_BY_FILM_QUERY =
            "SELECT user_id FROM likes WHERE film_id = ?";


    public JdbcFilmRepository(
            JdbcTemplate jdbc,
            FilmRowMapper mapper,
            JdbcGenreRepository genreRepository,
            JdbcLikeRepository likeRepository,
            JdbcRatingMpaRepository ratingRepository
    ) {
        super(jdbc, mapper);
        this.genreRepository = genreRepository;
        this.likeRepository = likeRepository;
        this.ratingRepository = ratingRepository;
    }


    @Override
    public Film create(Film film) {
        Long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRating().getId() // в таблицу вставляем ID рейтинга
        );
        film.setId(id);
        // 2. Сохраняем жанры  add  list <Genre>
        saveGenres(film);
        // 3. Сохраняем лайки
        saveLikes(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRating().getId(),
                film.getId()
        );

        saveGenres(film);
        saveLikes(film);
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        Collection<Film> films = findMany(FIND_ALL_QUERY);
        films.forEach(this::loadAdditionalData);
        return films;
    }

    @Override
    public boolean delete(Long id) {
        return delete(DELETE_QUERY, id);
    }

    @Override
    public Optional<Film> findById(Long id) {
        Optional<Film> filmOtional = findOne(FIND_BY_FILM_ID_QUERY, id);
        filmOtional.ifPresent(this::loadAdditionalData);
        return filmOtional;
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        Collection<Film> films = findMany(GET_POPULAR_FILM_QUERY, count);
        films.forEach(this::loadAdditionalData);
        return films;
    }

    @Override
    public boolean existsById(long id) {
        String sql = "SELECT COUNT(*) FROM films WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }


    private void loadAdditionalData(Film film) {
        List<Genre> genres = jdbc.query(
                FIND_GENRES_BY_FILM_QUERY,
                (rs, rowNum) -> new Genre(
                        rs.getLong("genre_id"),
                        rs.getString("name")
                ),
                film.getId()
        );
        film.setGenres(genres);

        Set<Long> likes = new HashSet<>(jdbc.query(
                FIND_LIKES_BY_FILM_QUERY,
                (rs, rowNum) -> rs.getLong("user_id"),
                film.getId()
        ));
        film.setLikes(likes);

    }

    private void saveGenres(Film film) {
        // удаляем старые жанры
        jdbc.update(DELETE_GENRES_QUERY, film.getId());

        Set<Long> uniqueGenreIds = new HashSet<>();
        List<Genre> uniqueGenres = new ArrayList<>();
        // осталвляем уникальные
        for (Genre genre : film.getGenres()) {
            if (uniqueGenreIds.add(genre.getId())) {
                uniqueGenres.add(genre);
            }
        }
        if (!uniqueGenres.isEmpty()) {
            jdbc.batchUpdate(
                    INSERT_GENRES_QUERY,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            Genre genre = uniqueGenres.get(i);
                            ps.setLong(1, film.getId());
                            ps.setLong(2, genre.getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return uniqueGenres.size();
                        }

                    }
            );
        }

    }


    private void saveLikes(Film film) {
        // Удаляем старые лайки
        likeRepository.removeAllLikes(film.getId());

        // Добавляем новые лайки
        for (Long userId : film.getLikes()) {
            likeRepository.addLike(film.getId(), userId);
        }
    }

    @Override
    public boolean existsByNameAndReleaseDate(String name, LocalDate releaseDate) {
        String sql = "SELECT COUNT(*) FROM films WHERE LOWER(TRIM(name)) = LOWER(TRIM(?)) AND release_date = ?";
        Integer count = jdbc.queryForObject(
                sql,
                Integer.class,
                name.trim(),
                releaseDate
        );
        return count != null && count > 0;
    }


}
