package ru.yandex.practicum.filmorate.dal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.RatingMpaRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        JdbcFilmRepository.class,
        JdbcGenreRepository.class,
        JdbcLikeRepository.class,
        JdbcRatingMpaRepository.class,
        FilmRowMapper.class,
        GenreRowMapper.class,
        RatingMpaRowMapper.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JdbcFilmRepositoryIntegrationTest {

    @Autowired
    private JdbcFilmRepository filmRepository;

    @Autowired
    private JdbcLikeRepository likeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Film createTestFilm(String name, String description, LocalDate releaseDate, Long duration, RatingMpa mpa) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        film.setMpa(mpa);
        film.setGenres(new HashSet<>());
        film.setLikes(new HashSet<>());
        return film;
    }

    @Test
    public void testCreateFilm() {
        Film film = createTestFilm(
                "New Test Film",
                "New test description",
                LocalDate.of(2022, 3, 15),
                140L,
                new RatingMpa(1L, "G")
        );
        film.getGenres().addAll(Arrays.asList(
                new Genre(1L, "Комедия"),
                new Genre(2L, "Драма")
        ));

        Film createdFilm = filmRepository.create(film);

        assertThat(createdFilm).isNotNull();
        assertThat(createdFilm.getId()).isNotNull().isPositive();
        assertThat(createdFilm.getName()).isEqualTo("New Test Film");
        assertThat(createdFilm.getDescription()).isEqualTo("New test description");
        assertThat(createdFilm.getReleaseDate()).isEqualTo(LocalDate.of(2022, 3, 15));
        assertThat(createdFilm.getDuration()).isEqualTo(140L);
        assertThat(createdFilm.getMpa().getId()).isEqualTo(1L);
        assertThat(createdFilm.getGenres()).hasSize(2);
    }

    @Test
    public void testFindFilmById() {
        // Убедимся, что данные из data.sql загружены
        Long filmId = 1L;
        Optional<Film> filmOpt = filmRepository.findById(filmId);
        assertThat(filmOpt).isPresent();

        Film film = filmOpt.get();
        assertThat(film.getId()).isEqualTo(filmId);
        assertThat(film.getName()).isEqualTo("film1");
        assertThat(film.getMpa().getId()).isEqualTo(2L);
        assertThat(film.getGenres()).hasSize(2);
    }

    @Test
    public void testFindAllFilms() {
        // Сначала создадим новый фильм
        Film newFilm = createTestFilm(
                "New Film",
                "New description",
                LocalDate.of(2023, 1, 1),
                120L,
                new RatingMpa(3L, "PG-13")
        );
        filmRepository.create(newFilm);

        Collection<Film> films = filmRepository.findAll();
        assertThat(films).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    public void testUpdateFilm() {
        // Создаем фильм для обновления
        Film film = createTestFilm(
                "Original Film",
                "Original Description",
                LocalDate.of(2020, 1, 1),
                120L,
                new RatingMpa(1L, "G")
        );
        Film createdFilm = filmRepository.create(film);

        // Обновляем данные
        createdFilm.setName("Updated Film");
        createdFilm.setDescription("Updated Description");
        createdFilm.setReleaseDate(LocalDate.of(2021, 1, 1));
        createdFilm.setDuration(150L);
        createdFilm.setMpa(new RatingMpa(2L, "PG"));
        createdFilm.getGenres().add(new Genre(1L, "Комедия"));

        Film updatedFilm = filmRepository.update(createdFilm);

        assertThat(updatedFilm.getId()).isEqualTo(createdFilm.getId());
        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedFilm.getReleaseDate()).isEqualTo(LocalDate.of(2021, 1, 1));
        assertThat(updatedFilm.getDuration()).isEqualTo(150L);
        assertThat(updatedFilm.getMpa().getId()).isEqualTo(2L);
        assertThat(updatedFilm.getGenres()).hasSize(1);
    }

    @Test
    public void testDeleteFilm() {
        Film film = createTestFilm(
                "Film to Delete",
                "Delete description",
                LocalDate.of(2019, 5, 5),
                100L,
                new RatingMpa(1L, "G")
        );
        Film createdFilm = filmRepository.create(film);
        Long filmId = createdFilm.getId();

        boolean deleted = filmRepository.delete(filmId);
        assertThat(deleted).isTrue();
        assertThat(filmRepository.findById(filmId)).isEmpty();
    }

    @Test
    public void testDeleteNonExistentFilm() {
        boolean deleted = filmRepository.delete(99999L);
        assertThat(deleted).isFalse();
    }

    @Test
    public void testGetPopularFilms() {
        // Очистим таблицы от данных data.sql
        jdbcTemplate.update("DELETE FROM likes");
        jdbcTemplate.update("DELETE FROM film_genre");
        jdbcTemplate.update("DELETE FROM films");

        // Перезапустим sequence
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");

        // Создаем новые фильмы
        Film film1 = createTestFilm(
                "Popular Film",
                "Popular Description",
                LocalDate.of(2020, 1, 1),
                120L,
                new RatingMpa(1L, "G")
        );
        Film film2 = createTestFilm(
                "Less Popular Film",
                "Less Popular Description",
                LocalDate.of(2021, 1, 1),
                150L,
                new RatingMpa(2L, "PG")
        );

        Film createdFilm1 = filmRepository.create(film1);
        Film createdFilm2 = filmRepository.create(film2);

        // Добавляем лайки
        likeRepository.addLike(createdFilm1.getId(), 1L);
        likeRepository.addLike(createdFilm1.getId(), 2L);
        likeRepository.addLike(createdFilm1.getId(), 3L);
        likeRepository.addLike(createdFilm2.getId(), 1L);

        Collection<Film> popularFilms = filmRepository.getPopularFilms(2);
        List<Film> popularList = new ArrayList<>(popularFilms);

        assertThat(popularList).hasSize(2);
        assertThat(popularList.get(0).getId()).isEqualTo(createdFilm1.getId());
        assertThat(popularList.get(1).getId()).isEqualTo(createdFilm2.getId());
    }
}