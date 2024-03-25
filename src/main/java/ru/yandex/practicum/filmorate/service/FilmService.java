package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;


    public Film createFilm(Film film) {
        log.info("Создание фильма.");
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        log.info("Обновление данных о фильме.");
        return filmStorage.updateFilm(film);
    }

    public Collection<Film> getAllFilms() {
        log.info("Список фильмов.");
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int id) {
        log.info("Вывод фильма по его id.");
        return filmStorage.getFilmById(id);
    }

    public Film addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (userStorage.getUserById(userId) != null) {
            film.getUsersLikes().add(userId);
            log.info("Пользователь с id: {} поставил лайк фильму с id {}", userId, filmId);
        }
        return film;
    }

    public Film removeLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);

        if (!film.getUsersLikes().contains(userId)) {
            throw new DataNotFoundException("Ошибка. Пользователь не ставил лайк фильму.");
        }
        film.getUsersLikes().remove(userId);
        log.info("Пользователь с id: {} удалил лайк фильма с id {}", userId, filmId);
        return film;
    }

    public List<Film> getPopular(int count) {
        log.info("Список популярных фильмов.");
        return filmStorage.getAllFilms().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getUsersLikes().size(), o1.getUsersLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
