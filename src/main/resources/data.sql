-- Предустановленные рейтинги MPA
MERGE INTO ratings_mpa (mpa_id, name) KEY(name) VALUES
 (1, 'G'),            -- General Audiences
 (2, 'PG'),           -- Parental Guidance Suggested
 (3, 'PG-13'),        -- Parents Strongly Cautioned
 (4, 'R'),            -- Restricted
 (5, 'NC-17');        -- Adults Only


-- Предустановленные жанры
MERGE INTO genres (genre_id, name) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');
