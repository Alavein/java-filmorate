package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

@SpringBootTest
class FilmControllerTest {

    @Autowired
    private FilmController controller;
    @Autowired
    private UserController userController;
    Film film = new Film(1, "Фильм", "Описание",
            LocalDate.of(2024, 1, 1), 100, new HashSet<>());
    User user = new User(1, "email@mail.ru", "login",
            "Name", LocalDate.of(1995, 10, 7), new HashSet<>());

    @DisplayName("Некорректное название фильма (пустое название).")
    @Test
    void create_EmptyName() {
        film.setName("");

        assertEquals("Ошибка. Название фильма не указано.", assertThrows(ValidationException.class, () ->
                controller.createFilm(film)).getMessage());
    }

    @DisplayName("Обновление данных о фильме.")
    @Test
    void update_Film() {
        Film updateFilm = new Film(1, "Фильм2",
                "Описание2",
                LocalDate.of(1990, 1, 1), 60, new HashSet<>());

        controller.createFilm(film);
        controller.updateFilm(updateFilm);

        assertEquals("Описание2", updateFilm.getDescription());
        assertEquals("Фильм2", updateFilm.getName());
        assertEquals(LocalDate.of(1990, 1, 1), updateFilm.getReleaseDate());
        assertEquals(60, updateFilm.getDuration());
    }

    @DisplayName("Некорректное название фильма (слишком длинное).")
    @Test
    void create_DescriptionMoreThan200() {
        film.setDescription("Огромное некорректное описание с деталями, излишками, именами актеров, " +
                "другими фильмами, где играли актеры, информацией о саундтреках и куче прочей неинтересной ерунды. " +
                "Все не то, и все не так. И пройти не должно.  ");

        assertEquals("Ошибка. Описание фильма должно быть меньше 200 символов.",
                (assertThrows(RuntimeException.class, () -> controller.createFilm(film)).getMessage()));
    }

    @DisplayName("Некорректная дата релиза.")
    @Test
    void create_WrongDateRelease() {
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        assertEquals("Ошибка. Некорректная дата релиза фильма.",
                ((assertThrows(RuntimeException.class, () -> controller.createFilm(film))).getMessage()));
    }

    @DisplayName("Продолжительность фильма отрицательная")
    @Test
    void create_WrongDuration() {
        film.setDuration(-10);
        assertEquals("Ошибка. Продолжительность фильма должна быть положительной (более 1 секунды).",
                ((assertThrows(RuntimeException.class, () -> controller.createFilm(film))).getMessage()));
    }

    @DisplayName("Проставление лайка фильму.")
    @Test
    void put_AddALikeToFilm() {
        userController.createUser(user);
        controller.createFilm(film);
        controller.addFilmLike(film.getId(), user.getId());

        assertEquals(1, film.getUsersLikes().size());
    }

    @DisplayName("Удаление лайка.")
    @Test
    void delete_RemoveLikeFromFilm() {
        userController.createUser(user);
        controller.createFilm(film);
        controller.addFilmLike(film.getId(), user.getId());
        controller.removeFilmLike(film.getId(), user.getId());
        assertEquals(0, film.getUsersLikes().size());
    }

    @DisplayName("Список популярных фильмов.")
    @Test
    void get_PopularMovies() {
        userController.createUser(user);
        controller.createFilm(film);
        controller.addFilmLike(film.getId(), user.getId());
        List<Film> popularMoviesList = controller.getPopular(1);
        assertEquals(1, popularMoviesList.size());
    }
}
