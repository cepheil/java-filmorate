INSERT INTO films (name, description, release_date, duration, mpa_rating) VALUES
('Film 1', 'Description for Film 1', '2025-01-01', 120, 'G'),
('Film 2', 'Description for Film 2', '2025-02-01', 150, 'PG'),
('Film 3', 'Description for Film 3', '2025-03-01', 180, 'PG-13');

INSERT INTO genres (name) VALUES
('Genre 1'),
('Genre 2'),
('Genre 3');

INSERT INTO film_genres (film_id, genre_id) VALUES
(1, 1),
(1, 2),
(2, 2),
(2, 3),
(3, 1),
(3, 3);

INSERT INTO users (email, login, name, birthday) VALUES
('user1@example.com', 'user1', 'User 1', '1990-01-01'),
('user2@example.com', 'user2', 'User 2', '1990-02-01'),
('user3@example.com', 'user3', 'User 3', '1990-03-01');

INSERT INTO likes (film_id, user_id) VALUES
(1, 1),
(1, 2),
(2, 2),
(2, 3),
(3, 1),
(3, 3);

INSERT INTO friends (user_id, friend_id, status) VALUES
(1, 2, 'CONFIRMED'),
(1, 3, 'PENDING'),
(2, 3, 'CONFIRMED');
