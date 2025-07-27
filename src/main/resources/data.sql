-- 1. Сначала заполняем таблицы без внешних ключей
-- Рейтинги MPA
INSERT INTO mpa_ratings (mpa_id, name, description)
VALUES
    (1, 'G', 'Без возрастных ограничений'),
    (2, 'PG', 'Детям рекомендуется смотреть с родителями');

-- Жанры
INSERT INTO genres (genre_id, name)
VALUES
    (1, 'Комедия'),
    (2, 'Драма');

-- Пользователи
INSERT INTO users (user_id, email, login, name, birthday)
VALUES
    (1, 'user1@mail.ru', 'user1', 'User One', '1990-01-01'),
    (2, 'user2@mail.ru', 'user2', 'User Two', '1995-05-15'),
    (3, 'user3@mail.ru', 'user3', 'User Three', '2000-10-20');

-- 2. Затем заполняем таблицы с внешними ключами
-- Фильмы
INSERT INTO films (film_id, name, description, release_date, duration, mpa_id)
VALUES
    (1, 'Film 1', 'Description 1', '2000-01-01', 120, 1),
    (2, 'Film 2', 'Description 2', '2010-05-15', 90, 2);

-- 3. В конце - таблицы связей
-- Жанры фильмов
INSERT INTO film_genre (film_id, genre_id)
VALUES
    (1, 1),  -- Film 1: Комедия
    (1, 2),  -- Film 1: Драма
    (2, 2);  -- Film 2: Драма

-- Друзья
INSERT INTO friends (user_id, friend_id)
VALUES
    (1, 2),  -- user1 добавил user2
    (1, 3);  -- user1 добавил user3

-- Лайки
INSERT INTO likes (film_id, user_id)
VALUES
    (1, 1), (1, 2),
    (2, 1), (2, 3);