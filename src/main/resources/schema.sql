DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS friendship CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS films_users_relationship CASCADE;
DROP TABLE IF EXISTS genres_films CASCADE;
DROP TABLE IF EXISTS mpa CASCADE;

CREATE TABLE IF NOT EXISTS users (
	id       INT        GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	email    varchar    NOT NULL,
	login    varchar    NOT NULL,
	name     varchar,
	birthday DATE
);

CREATE TABLE IF NOT EXISTS friendship (
   id_users    INT NOT NULL,
   id_friends  INT NOT NULL,
   CONSTRAINT pk_friends PRIMARY KEY (id_users, id_friends),
   CONSTRAINT fk_friends1 FOREIGN KEY (id_users) REFERENCES users(id),
   CONSTRAINT fk_friends2 FOREIGN KEY (id_friends) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS mpa (
    id_mpa      INT     GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name_mpa    varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    id              INT             GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name            varchar          NOT NULL,
    description     varchar(200),
    release_date    DATE,
    duration        INT            CHECK (duration > 0),
    id_mpa          INT REFERENCES mpa(id_mpa) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS films_users_relationship (
    id_users INT REFERENCES users (id) ON DELETE CASCADE,
    id_films INT REFERENCES films (id) ON DELETE CASCADE,
    PRIMARY KEY (id_users, id_films)
);

CREATE TABLE IF NOT EXISTS genre (
    id           INT            PRIMARY KEY,
    genre_name   varchar        NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS genres_films (
    id_films    INT REFERENCES films(id)   ON DELETE CASCADE,
    id_genres   INT REFERENCES genre(id)   ON DELETE CASCADE
);