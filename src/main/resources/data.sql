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

INSERT INTO genre (genre_name) SELECT 'Драма'
WHERE NOT EXISTS (SELECT genre_name FROM GENRE WHERE genre_name = 'Драма');

INSERT INTO genre (genre_name) SELECT 'Комедия'
WHERE NOT EXISTS (SELECT genre_name FROM GENRE WHERE genre_name = 'Комедия');

INSERT INTO genre (genre_name) SELECT 'Мультфильм'
WHERE NOT EXISTS (SELECT genre_name FROM GENRE WHERE genre_name = 'Мультфильм');

INSERT INTO genre (genre_name) SELECT 'Документальный'
WHERE NOT EXISTS (SELECT genre_name FROM GENRE WHERE genre_name = 'Документальный');

INSERT INTO genre (genre_name) SELECT 'Триллер'
WHERE NOT EXISTS (SELECT genre_name FROM GENRE WHERE genre_name = 'Триллер');

INSERT INTO genre (genre_name) SELECT 'Боевик'
WHERE NOT EXISTS (SELECT genre_name FROM GENRE WHERE genre_name = 'Боевик');