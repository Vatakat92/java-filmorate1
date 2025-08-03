-- Clean up tables before inserting test data
DELETE FROM film_likes;
DELETE FROM friendships;
DELETE FROM film_genres;
DELETE FROM films;
DELETE FROM users;
DELETE FROM genres;
DELETE FROM mpa_ratings;

-- Insert MPA ratings
INSERT INTO mpa_ratings (id, name) VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

-- Insert genres
INSERT INTO genres (id, name) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

-- Insert test users (ID >= 1001)
INSERT INTO users (id, email, login, name, birthday) VALUES
(1001, 'test1@example.com', 'testuser1', 'Test User 1', '1990-01-01'),
(1002, 'test2@example.com', 'testuser2', 'Test User 2', '1985-05-15'),
(1003, 'test3@example.com', 'testuser3', 'Test User 3', '1995-12-25'),
(1004, 'admin@filmorate.com', 'admin', 'Administrator', '1980-06-10');

-- Insert test films (ID >= 1001)
INSERT INTO films (id, name, description, release_date, duration, mpa_rating_id) VALUES
(1001, 'The Matrix', 'A computer hacker learns from mysterious rebels about the true nature of his reality.', '1999-03-31', 136, 4),
(1002, 'Toy Story', 'A cowboy doll is profoundly threatened when a new spaceman figure supplants him as top toy.', '1995-11-22', 81, 1),
(1003, 'The Shawshank Redemption', 'Two imprisoned men bond over a number of years, finding solace and eventual redemption.', '1994-09-23', 142, 4),
(1004, 'Pulp Fiction', 'The lives of two mob hitmen, a boxer, and others intertwine in four tales of violence.', '1994-10-14', 154, 4),
(1005, 'Finding Nemo', 'After his son is captured, a timid clownfish sets out on a journey to find him.', '2003-05-30', 100, 1);

-- Insert film genres relationships (with updated film_id)
INSERT INTO film_genres (film_id, genre_id) VALUES
(1001, 6), -- The Matrix - Боевик
(1001, 4), -- The Matrix - Триллер
(1002, 3), -- Toy Story - Мультфильм
(1002, 1), -- Toy Story - Комедия
(1003, 2), -- The Shawshank Redemption - Драма
(1004, 2), -- Pulp Fiction - Драма
(1004, 4), -- Pulp Fiction - Триллер
(1005, 3), -- Finding Nemo - Мультфильм
(1005, 1); -- Finding Nemo - Комедия

-- Insert test friendships (updated user_id / friend_id)
INSERT INTO friendships (user_id, friend_id, status) VALUES
(1001, 1002, 'CONFIRMED'),
(1002, 1001, 'CONFIRMED'),
(1001, 1003, 'CONFIRMED'),
(1003, 1001, 'CONFIRMED'),
(1002, 1003, 'CONFIRMED'),
(1003, 1002, 'CONFIRMED');

-- Insert test film likes (updated film_id / user_id)
INSERT INTO film_likes (film_id, user_id) VALUES
(1001, 1001),
(1001, 1002),
(1001, 1003),
(1002, 1001),
(1002, 1004),
(1003, 1002),
(1003, 1003),
(1004, 1001),
(1005, 1004);
