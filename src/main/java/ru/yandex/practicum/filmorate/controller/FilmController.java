package ru.yandex.practicum.filmorate.controller;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film  createFilm(@Valid @RequestBody Film film) {
        log.info("Создание фильма.");
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Обновление данных о фильме.");
        return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Вывод списка фильмов.");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") Long id) {
        log.info("Вывод фильма по его id.");
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addFilmLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Праставление лайка.");
        filmService.addFilmLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeFilmLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Удаление лайка.");
        filmService.removeFilmLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") @Positive Long count) {
        log.info("Вывод списка популярных фильмов.");
        return filmService.getPopular(count);
    }
}
