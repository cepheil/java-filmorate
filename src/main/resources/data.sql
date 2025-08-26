-- Рейтинги MPA
MERGE INTO mpa_ratings (mpa_id, name, description)
VALUES
        (1, 'G', 'Без возрастных ограничений'),
        (2, 'PG', 'Детям рекомендуется смотреть с родителями'),
        (3, 'PG-13', 'Детям до 13 лет просмотр не желателен'),
        (4, 'R', 'Лицам до 17 лет обязательно присутствие взрослого'),
        (5, 'NC-17', 'Лицам до 18 лет просмотр запрещен');

-- Жанры
MERGE INTO genres (genre_id, name)
VALUES
    (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик');

--Типы событий
MERGE INTO event_type (type_id, name)
VALUES
    (1, 'LIKE'),
    (2, 'REVIEW'),
    (3, 'FRIEND');

--Операции
MERGE INTO operation (operation_id, name)
VALUES
    (1, 'REMOVE'),
    (2, 'ADD'),
    (3, 'UPDATE');

--Пользователи
--MERGE INTO USERS AS target
--USING (
--    SELECT 1 AS USER_ID, 'user1@example.com' AS EMAIL, 'user1' AS LOGIN, 'User One' AS NAME, '1990-01-01' AS BIRTHDAY
--    UNION ALL
--    SELECT 2 AS USER_ID, 'user2@example.com' AS EMAIL, 'user2' AS LOGIN, 'User Two' AS NAME, '1990-05-05' AS BIRTHDAY
--    UNION ALL
--    SELECT 3 AS USER_ID, 'user3@example.com' AS EMAIL, 'user3' AS LOGIN, 'User Three' AS NAME, '1990-03-03' AS BIRTHDAY
--) AS source
--ON (target.USER_ID = source.USER_ID)
--WHEN MATCHED THEN
--    UPDATE SET
--        target.EMAIL = source.EMAIL,
--        target.LOGIN = source.LOGIN,
--        target.NAME = source.NAME,
--        target.BIRTHDAY = source.BIRTHDAY
--WHEN NOT MATCHED THEN
--    INSERT (EMAIL, LOGIN, NAME, BIRTHDAY)
--    VALUES (source.EMAIL, source.LOGIN, source.NAME, source.BIRTHDAY);

--Фильмы
--MERGE INTO FILMS AS target
--USING (
--    SELECT 1 AS FILM_ID, 'Test Film 1' AS NAME, 'Test Description 1' AS DESCRIPTION, '2020-01-01' AS RELEASE_DATE, '120' AS DURATION, 1 AS MPA_ID
--    UNION ALL
--    SELECT 2 AS FILM_ID, 'Test Film 2' AS NAME, 'Test Description 2' AS DESCRIPTION, '2021-01-01' AS RELEASE_DATE, '150' AS DURATION, 2 AS MPA_ID
--) AS source
--ON (target.FILM_ID = source.FILM_ID)
--WHEN MATCHED THEN
--    UPDATE SET
--        target.NAME = source.NAME,
--        target.DESCRIPTION = source.DESCRIPTION,
--        target.RELEASE_DATE = source.RELEASE_DATE,
--        target.DURATION = source.DURATION,
--        target.MPA_ID = source.MPA_ID
--WHEN NOT MATCHED THEN
--    INSERT (NAME , DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
--    VALUES (source.NAME , source.DESCRIPTION, source.RELEASE_DATE, source.DURATION, source.MPA_ID);

--Друзья
--MERGE INTO friends (user_id, friend_id, confirmed) VALUES (1, 3, TRUE), (2, 3, TRUE);

--Лайки
--MERGE INTO likes (film_id, user_id) VALUES (1, 1), (1, 2);

--Жанры фильмов
--MERGE INTO film_genre (film_id, genre_id) VALUES (1, 1), (1, 2), (2, 3);