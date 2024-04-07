package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    private Genre buildGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    public Genre getGenre(Long id) {
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject("SELECT * FROM genre WHERE id = ?", this::buildGenre, id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Ошибка. Жанр с id " + id + " не найден.");
            throw new DataNotFoundException("Ошибка. Жанр с id " + id + " не найден.");
        }
        return genre;
    }

    public List<Genre> getGenreList() {
        return jdbcTemplate.query("SELECT * FROM genre", this::buildGenre)
                .stream()
                .sorted(Comparator.comparingLong(Genre::getId))
                .collect(Collectors.toList());
    }
}