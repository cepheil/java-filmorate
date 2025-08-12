-- Предустановленные рейтинги MPA
MERGE INTO ratings_mpa (mpa_id, name) KEY(mpa_id) VALUES (1, 'G');
MERGE INTO ratings_mpa (mpa_id, name) KEY(mpa_id) VALUES (2, 'PG');
MERGE INTO ratings_mpa (mpa_id, name) KEY(mpa_id) VALUES (3, 'PG-13');
MERGE INTO ratings_mpa (mpa_id, name) KEY(mpa_id) VALUES (4, 'R');
MERGE INTO ratings_mpa (mpa_id, name) KEY(mpa_id) VALUES (5, 'NC-17');

-- Предустановленные жанры
MERGE INTO genres (genre_id, name) KEY(genre_id) VALUES (1, 'Комедия');
MERGE INTO genres (genre_id, name) KEY(genre_id) VALUES (2, 'Драма');
MERGE INTO genres (genre_id, name) KEY(genre_id) VALUES (3, 'Мультфильм');
MERGE INTO genres (genre_id, name) KEY(genre_id) VALUES (4, 'Триллер');
MERGE INTO genres (genre_id, name) KEY(genre_id) VALUES (5, 'Документальный');
MERGE INTO genres (genre_id, name) KEY(genre_id) VALUES (6, 'Боевик');

-- Тестовые данные для пользователей
MERGE INTO users (id, email, login, name, birthday) KEY(id) VALUES (1, 'aaa@example.com', 'aaa', 'aaa aaa', '1985-04-15');
MERGE INTO users (id, email, login, name, birthday) KEY(id) VALUES (2, 'bbb@example.com', 'bbb', 'bbb bbb', '1988-07-22');
MERGE INTO users (id, email, login, name, birthday) KEY(id) VALUES (3, 'ccc@example.com', 'ccc', 'ccc ccc', '1992-11-03');

-- Тестовые данные для фильмов
MERGE INTO films (id, name, description, release_date, duration, mpa_id) KEY(id) VALUES (1, 'film1', 'description film1', '2019-06-10', 130, 2);
MERGE INTO films (id, name, description, release_date, duration, mpa_id) KEY(id) VALUES (2, 'film2', 'description film2', '2021-11-20', 110, 4);

-- Тестовые данные для дружбы
MERGE INTO friendships (user_id, friend_id, confirmed) KEY(user_id, friend_id) VALUES (1, 3, TRUE);
MERGE INTO friendships (user_id, friend_id, confirmed) KEY(user_id, friend_id) VALUES (2, 3, TRUE);

-- Тестовые данные для лайков
MERGE INTO likes (film_id, user_id) KEY(film_id, user_id) VALUES (1, 1);
MERGE INTO likes (film_id, user_id) KEY(film_id, user_id) VALUES (1, 2);

-- Тестовые данные для жанров фильмов
MERGE INTO film_genre (film_id, genre_id) KEY(film_id, genre_id) VALUES (1, 1);
MERGE INTO film_genre (film_id, genre_id) KEY(film_id, genre_id) VALUES (1, 2);
MERGE INTO film_genre (film_id, genre_id) KEY(film_id, genre_id) VALUES (2, 3);


-- Сброс sequence для таблиц
ALTER TABLE users ALTER COLUMN id RESTART WITH 4;
ALTER TABLE films ALTER COLUMN id RESTART WITH 3;