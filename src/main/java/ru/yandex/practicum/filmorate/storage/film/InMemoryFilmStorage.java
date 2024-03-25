package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int generatorFilmId = 0;

    private void filmValidation(Film film) {
        if (film.getReleaseDate() == null ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка. Некорректная дата релиза фильма.");
            throw new ValidationException("Ошибка. Некорректная дата релиза фильма.");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Ошибка. Название фильма не указано.");
            throw new ValidationException("Ошибка. Название фильма не указано.");
        }
        if (film.getDuration() <= 0) {
            log.warn("Ошибка. Продолжительность фильма должна быть положительной (более 1 секунды).");
            throw new ValidationException("Ошибка. Продолжительность фильма должна быть положительной " +
                    "(более 1 секунды).");
        }
        if (film.getDescription() == null || film.getDescription().isBlank() || film.getDescription().length() > 200) {
            log.warn("Ошибка. Описание фильма должно быть меньше 200 символов.");
            throw new ValidationException("Ошибка. Описание фильма должно быть меньше 200 символов.");
        }
    }

    @Override
    public Film createFilm(Film film) {
        filmValidation(film);
        log.info("Добавляем фильм: {}", film);
        film.setId(generateFilmId());
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        filmValidation(film);
        log.info("Обновляем данные фильма: {}", film);
        Integer id = film.getId();
        if (!films.containsKey(id)) {
            log.info("Ошибка. Фильм не найден: {}", film);
            throw new DataNotFoundException("Ошибка. Фильм не найден");
        }
        films.put(id, film);
        log.info("Данные о фильме успешно обновлены: {}", film);
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film getFilmById(int id) {
        Film film = films.get(id);
        if (film == null) {
            throw new DataNotFoundException("Ошибка. Фильм не найден.");
        }
        log.info("Фильм с id:" + id + "успешно найден.");
        return film;
    }

    private int generateFilmId() {
        return ++generatorFilmId;
    }
}
