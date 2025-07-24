-- Предустановленные рейтинги MPA

MERGE INTO ratings_mpa (mpa_id, name) KEY(name) VALUES (1, 'G');            -- General Audiences
MERGE INTO ratings_mpa (mpa_id, name) KEY(name) VALUES (2, 'PG');           -- Parental Guidance Suggested
MERGE INTO ratings_mpa (mpa_id, name) KEY(name) VALUES (3, 'PG-13');        -- Parents Strongly Cautioned
MERGE INTO ratings_mpa (mpa_id, name) KEY(name) VALUES (4, 'R');            -- Restricted
MERGE INTO ratings_mpa (mpa_id, name) KEY(name) VALUES (5, 'NC-17');        -- Adults Only


-- Предустановленные жанры фильмов
MERGE INTO genres (genre_id, name) VALUES (1, 'Комедия');
MERGE INTO genres (genre_id, name) VALUES (2, 'Драма');
MERGE INTO genres (genre_id, name) VALUES (3, 'Триллер');
MERGE INTO genres (genre_id, name) VALUES (4, 'Боевик');
MERGE INTO genres (genre_id, name) VALUES (5, 'Ужасы');
MERGE INTO genres (genre_id, name) VALUES (6, 'Фантастика');
MERGE INTO genres (genre_id, name) VALUES (7, 'Документальный');
MERGE INTO genres (genre_id, name) VALUES (8, 'Мультфильм');