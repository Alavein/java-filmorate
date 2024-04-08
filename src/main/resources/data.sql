INSERT INTO mpa (name_mpa) SELECT 'G'
WHERE NOT EXISTS (SELECT name_mpa FROM MPA WHERE name_mpa = 'G');

INSERT INTO mpa (name_mpa) SELECT 'PG'
WHERE NOT EXISTS (SELECT name_mpa FROM MPA WHERE name_mpa = 'PG');

INSERT INTO mpa (name_mpa) SELECT 'PG-13'
WHERE NOT EXISTS (SELECT name_mpa FROM MPA WHERE name_mpa = 'PG-13');

INSERT INTO mpa (name_mpa) SELECT 'R'
WHERE NOT EXISTS (SELECT name_mpa FROM MPA WHERE name_mpa = 'R');

INSERT INTO mpa (name_mpa) SELECT 'NC-17'
WHERE NOT EXISTS (SELECT name_mpa FROM MPA WHERE name_mpa = 'NC-17');

INSERT INTO genre (id, genre_name)
SELECT 1, 'Комедия'
WHERE NOT EXISTS (SELECT genre_name FROM genre WHERE genre_name = 'Комедия');

INSERT INTO genre (id, genre_name)
SELECT 2, 'Драма'
WHERE NOT EXISTS (SELECT genre_name FROM genre WHERE genre_name = 'Драма');

INSERT INTO genre (id, genre_name)
SELECT 3, 'Мультфильм'
WHERE NOT EXISTS (SELECT genre_name FROM genre WHERE genre_name = 'Мультфильм');

INSERT INTO genre (id, genre_name)
SELECT 4, 'Триллер'
WHERE NOT EXISTS (SELECT genre_name FROM genre WHERE genre_name = 'Триллер');

INSERT INTO genre (id, genre_name)
SELECT 5, 'Документальный'
WHERE NOT EXISTS (SELECT genre_name FROM genre WHERE genre_name = 'Документальный');

INSERT INTO genre (id, genre_name)
SELECT 6, 'Боевик'
WHERE NOT EXISTS (SELECT genre_name FROM genre WHERE genre_name = 'Боевик');