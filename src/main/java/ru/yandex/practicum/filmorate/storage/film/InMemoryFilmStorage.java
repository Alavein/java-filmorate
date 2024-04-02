package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("inMemoryFilmStorage")
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final List<Film> films = new ArrayList<>();
    private Long generatedId = 0L;

    @Override
    public Film createFilm(Film film) {
        film.setId(generateIdFilm());
        films.add(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        try {
            for (Film f : films) {
                if (Objects.equals(f.getId(), film.getId())) {
                    break;
                } else {
                    throw new ValidationException("Ошибка. Фильм не найден.");
                }
            }
            films.removeIf(f -> Objects.equals(f.getId(), film.getId()));
            films.add(film);
        } catch (ValidationException exception) {
            log.info(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return films;
    }

    @Override
    public Film getFilmById(Long id) {
        Optional<Film> optionalFilm = films.stream()
                .filter(film -> Objects.equals(film.getId(), id))
                .findFirst();
        if (optionalFilm.isPresent()) {
            return optionalFilm.get();
        } else {
            throw new DataNotFoundException("Ошибка. Фильм не найден.");
        }
    }

    @Override
    public void addFilmLike(Long id, Long userId) {
        if (userId <= 0) {
            throw new DataNotFoundException("Ошибка. Пользователь не найден.");
        } else {
            getFilmById(id).setLikeFilm(userId);
        }
    }

    @Override
    public void removeFilmLike(Long id, Long userId) {
        if (userId <= 0) {
            throw new DataNotFoundException("Ошибка. Пользователь не найден.");
        } else {
            getFilmById(id).getUsersLikes().remove(userId);
        }
    }

    @Override
    public List<Film> getPopular(Long count) {
        return films.stream()
                .sorted((film1, film2) -> film2.getUsersLikes().size() - film1.getUsersLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private Long generateIdFilm() {
        return ++generatedId;
    }
}