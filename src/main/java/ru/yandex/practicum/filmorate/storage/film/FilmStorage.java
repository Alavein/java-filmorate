package ru.yandex.practicum.filmorate.storage.film;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmStorage {

    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmById(Long id);

    void addFilmLike(Long id, Long userId);

    void removeFilmLike(Long id, Long userId);

    List<Film> getPopular(Long count);
}
