package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.genre.JdbcGenreRepository;
import ru.yandex.practicum.filmorate.storage.mpa.JdbcMpaRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        JdbcFilmRepository.class,
        FilmRowMapper.class,
        JdbcGenreRepository.class,
        GenreRowMapper.class,
        JdbcMpaRepository.class,
        MpaRatingRowMapper.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JdbcFilmRepositoryIntegrationTest {

    @Autowired
    private JdbcFilmRepository filmRepository;

    @BeforeEach
    public void setUp() {
        filmRepository.deleteAllFilms();
    }

    @Test
    public void testCreateFilm() {
        Film film = createUniqueFilm("Create Test");
        Film createdFilm = filmRepository.createFilm(film);
        assertThat(createdFilm).isNotNull();
        assertThat(createdFilm.getId()).isNotNull().isPositive();
        assertThat(createdFilm.getName()).isEqualTo("Create Test");
        assertThat(createdFilm.getDescription()).isEqualTo("Create Test Description");
        assertThat(createdFilm.getReleaseDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(createdFilm.getDuration()).isEqualTo(120);
        assertThat(createdFilm.getMpa().getId()).isEqualTo(1L);
        assertThat(createdFilm.getGenres()).hasSize(2);
    }

    @Test
    public void testFindFilmById() {
        Film film = createUniqueFilm("Find Test");
        Film createdFilm = filmRepository.createFilm(film);
        Optional<Film> foundFilm = filmRepository.getFilmById(createdFilm.getId());
        assertThat(foundFilm).isPresent();
        assertThat(foundFilm.get().getId()).isEqualTo(createdFilm.getId());
        assertThat(foundFilm.get().getName()).isEqualTo("Find Test");
    }

    @Test
    public void testFindAllFilms() {
        Film film1 = createUniqueFilm("FindAll1");
        Film film2 = createUniqueFilm("FindAll2");
        filmRepository.createFilm(film1);
        filmRepository.createFilm(film2);
        assertThat(filmRepository.findAllFilms()).hasSize(2);
    }

    @Test
    public void testUpdateFilm() {
        Film original = createUniqueFilm("Update Original");
        Film created = filmRepository.createFilm(original);
        Film updated = Film.builder()
                .id(created.getId())
                .name("Updated Name")
                .description("Updated Description")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(150)
                .mpa(new MpaRating(2L, "PG", null))
                .genres(new HashSet<>())
                .build();
        Film result = filmRepository.updateFilm(updated);
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    public void testDeleteFilm() {
        Film film = createUniqueFilm("Delete Test");
        Film created = filmRepository.createFilm(film);
        assertThat(filmRepository.deleteFilm(created.getId())).isTrue();
        assertThat(filmRepository.getFilmById(created.getId())).isEmpty();
    }

    @Test
    public void testDeleteNonExistentFilm() {
        assertThat(filmRepository.deleteFilm(999L)).isFalse();
    }

    @Test
    public void testGetPopularFilms() {
        Film film1 = createUniqueFilm("Popular1");
        Film film2 = createUniqueFilm("Popular2");
        filmRepository.createFilm(film1);
        filmRepository.createFilm(film2);
        assertThat(filmRepository.getPopularFilms(2)).hasSize(2);
    }


    private Film createUniqueFilm(String name) {
        return Film.builder()
                .name(name)
                .description(name + " Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpa(new MpaRating(1L, "G", "General Audiences"))
                .genres(new HashSet<>(Set.of(
                        new Genre(1L, "Комедия"),
                        new Genre(2L, "Драма")
                )))
                .directors(new HashSet<>())
                .build();
    }
}

