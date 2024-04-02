package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;

    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }


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
        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        filmValidation(film);
        return filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    @Override
    public void addFilmLike(Long id, Long userId) {
        filmStorage.addFilmLike(id, userId);
    }

    @Override
    public void removeFilmLike(Long id, Long userId) {
        filmStorage.removeFilmLike(id, userId);
    }

    @Override
    public List<Film> getPopular(Long count) {
        return filmStorage.getPopular(count);
    }
}
