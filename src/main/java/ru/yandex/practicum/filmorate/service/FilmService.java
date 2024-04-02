package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

@Service
public interface FilmService {

    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmById(Long id);

    void addFilmLike(Long id, Long userId);

    void removeFilmLike(Long id, Long userId);

    List<Film> getPopular(Long  count);
}
