package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa getMpa(Integer id) {
        Mpa mpa;
        try {
            mpa = jdbcTemplate.queryForObject("SELECT * FROM mpa WHERE id_mpa = ?", this::buildMpa, id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Ошибка. Рейтинг с id " + id + "не найден.");
            throw new DataNotFoundException("Ошибка. Рейтинг с id " + id + "не найден.");
        }
        return mpa;
    }

    private Mpa buildMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("id_mpa"))
                .name(rs.getString("name_mpa"))
                .build();
    }

    public List<Mpa> getMpaList() {
        return jdbcTemplate.query("SELECT * FROM mpa", this::buildMpa);
    }
}
