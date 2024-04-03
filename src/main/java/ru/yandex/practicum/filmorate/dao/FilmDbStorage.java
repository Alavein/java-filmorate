package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private boolean checkFilm(Long filmId) {
        try {
            jdbcTemplate.queryForObject("SELECT f.*, mpa.name_mpa FROM films AS f " +
                    "JOIN mpa ON f.id_mpa=mpa.id_mpa WHERE id = ?", this::buildFilm, filmId);
            return false;
        } catch (DataNotFoundException exception) {
            return true;
        }
    }

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Long id = insert.executeAndReturnKey(film.filmToMap(film)).longValue();
        film.setId(id);
        if (film.getGenres().isEmpty()) {
            for (Genre g : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO genres_films (id_films, id_genres) " +
                        "VALUES (?, ?)", id, g.getId());
            }
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (checkFilm(film.getId())) {
            log.info("Ошибка. Фильм с id = " + film.getId() + "не найден.");
            throw new DataNotFoundException("Ошибка. Фильм с id = " + film.getId() + "не найден.");
        }
        jdbcTemplate.update("DELETE FROM genres_films WHERE id_films = ?", film.getId());
        if (film.getGenres() != null && film.getGenres().size() != 0) {
            film.getGenres().forEach(genre -> jdbcTemplate.update("INSERT INTO genres_films (id_films, id_genres)" +
                    " VALUES (?, ?)", film.getId(), genre.getId()));
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query("SELECT f.*, mpa.name_mpa FROM films AS f JOIN mpa ON f.id_mpa=mpa.id_mpa",
                this::buildFilm);
    }

    @Override
    public Film getFilmById(Long id) {
        if (checkFilm(id)) {
            log.info("Ошибка. Фильм с id = " + id + "не найден.");
            throw new DataNotFoundException("Ошибка. Фильм с id = " + id + "не найден.");
        }
        String sql = "SELECT f.*, mpa.name_mpa FROM films AS f " +
                "JOIN mpa ON f.id_mpa=mpa.id_mpa WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, this::buildFilm, id);
    }

    @Override
    public void addFilmLike(Long id, Long userId) {
        if (checkFilm(id)) {
            log.info("Ошибка. Фильм с id = " + id + "не найден.");
            throw new DataNotFoundException("Ошибка. Фильм с id = " + id + "не найден.");
        }
        try {
            jdbcTemplate.update("INSERT INTO films_users_relationship (id_users, id_films)" +
                            " VALUES (?, ?)",
                    userId, id
            );
        } catch (DuplicateKeyException e) {
            log.info("Ошибка. Нельзя поставить лайк фильму дважды.");
            throw new ValidationException("Ошибка. Нельзя поставить лайк фильму дважды.");
        }
    }

    @Override
    public void removeFilmLike(Long id, Long userId) {
        if (checkFilm(id)) {
            log.info("Ошибка. Фильм с id " + id + " не найден. ");
            throw new DataNotFoundException("Ошибка. Фильм с id " + id + " не найден. ");
        }
        String sql = "DELETE FROM films_users_relationship WHERE id_users = ? AND id_films + ?";
        if (jdbcTemplate.update(sql, userId, id) == 0) {
            log.info("Ошибка. У фильма нет лайков.");
            throw new DataNotFoundException("Ошибка. У фильма нет лайков.");
        }
    }

    @Override
    public List<Film> getPopular(Long count) {
        return getAllFilms()
                .stream()
                .sorted((film1, film2) -> film2.getUsersLikes().size() - film1.getUsersLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private Genre buildGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    private Set<Genre> getGenresById(Long id) {
        String sqlStr = "SELECT g.id, g.genre_name FROM genres_films AS gf " +
                "JOIN genre AS g ON gf.id_genres=g.id WHERE gf.id_films = ?";
        return new HashSet<>(jdbcTemplate.query(sqlStr, this::buildGenre, id));
    }

    private Film buildFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("id_mpa"), rs.getString("name_mpa")))
                .build();

        film.getUsersLikes().addAll(jdbcTemplate.query(
                "SELECT id_users FROM films_users_relationship WHERE id_films = ?",
                (rs1, rowNum1) -> rs1.getLong("id_users"),
                film.getId()
        ));
        film.getGenres().addAll(getGenresById(film.getId()));
        return film;
    }
}
